package com.santea.admin;

import com.santea.shared.events.DomainEvent;
import com.santea.shared.events.RabbitEventPublisher;
import com.santea.shared.model.PasswordHashing;
import com.santea.shared.model.UserAccountPatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/admin-users")
public class AdminUserController {
    private final AdminUserRepository repository;
    private final AdminSequenceService sequenceService;
    private final RabbitEventPublisher events;

    public AdminUserController(AdminUserRepository repository, AdminSequenceService sequenceService, RabbitEventPublisher events) {
        this.repository = repository;
        this.sequenceService = sequenceService;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<AdminUser> create(@Valid @RequestBody AdminUser value) {
        value.setId(sequenceService.nextId());
        value.setPassword(PasswordHashing.hash(value.getPassword()));
        value.initializeTimestamps();
        value.setRole("ROLE_ADMIN");
        AdminUser saved = repository.save(value);
        publish("admin-user.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/admin-users/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<AdminUser> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AdminUser get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public AdminUser update(@PathVariable Long id, @Valid @RequestBody AdminUser value) {
        AdminUser existing = find(id);
        if (isActive(existing) && (!isActive(value)) && countActiveAdmins() <= 1) {
            throw new ResponseStatusException(CONFLICT, "Impossible de désactiver le dernier administrateur actif");
        }
        value.setId(id);
        value.setPassword(PasswordHashing.hash(value.getPassword()));
        value.setCreatedAt(existing.getCreatedAt());
        value.initializeTimestamps();
        value.setRole("ROLE_ADMIN");
        AdminUser saved = repository.save(value);
        publish("admin-user.updated.v1", id);
        return saved;
    }

    @PatchMapping("/{id}/account")
    public AdminUser updateAccount(@PathVariable Long id, @Valid @RequestBody UserAccountPatch patch) {
        AdminUser admin = find(id);
        if (Boolean.FALSE.equals(patch.getActive()) && isActive(admin) && countActiveAdmins() <= 1) {
            throw new ResponseStatusException(CONFLICT, "Impossible de désactiver le dernier administrateur actif");
        }
        admin.apply(patch);
        admin.setRole("ROLE_ADMIN");
        AdminUser saved = repository.save(admin);
        publish("admin-user.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        AdminUser admin = find(id);
        if (admin.getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(CONFLICT, "Un administrateur ne peut pas supprimer son propre compte");
        }
        if (isActive(admin) && countActiveAdmins() <= 1) {
            throw new ResponseStatusException(CONFLICT, "Impossible de supprimer le dernier administrateur actif");
        }
        repository.delete(admin);
        publish("admin-user.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private AdminUser find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Admin user not found"));
    }

    private long countActiveAdmins() {
        return repository.findAll().stream().filter(this::isActive).count();
    }

    private boolean isActive(AdminUser admin) {
        return !admin.isBanned() && admin.getDeletedAt() == null;
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("adminUserId", id)));
    }
}
