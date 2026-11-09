package com.santea.pharmacy;

import com.santea.shared.events.DomainEvent;
import com.santea.shared.events.RabbitEventPublisher;
import com.santea.shared.model.PasswordHashing;
import com.santea.shared.model.UserAccountPatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/pharmaciens")
public class PharmacienController {
    private final PharmacienRepository repository;
    private final RabbitEventPublisher events;

    public PharmacienController(PharmacienRepository repository, RabbitEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<Pharmacien> create(@Valid @RequestBody Pharmacien value) {
        prepare(value);
        Pharmacien saved = repository.save(value);
        publish("pharmacien.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/pharmaciens/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Pharmacien> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Pharmacien get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public Pharmacien update(@PathVariable Long id, @Valid @RequestBody Pharmacien value) {
        Pharmacien existing = find(id);
        prepare(value);
        value.getUser().setCreatedAt(existing.getUser().getCreatedAt());
        value.getUser().initializeTimestamps();
        value.setId(id);
        value.setCreatedAt(existing.getCreatedAt());
        value.setUpdatedAt(OffsetDateTime.now());
        Pharmacien saved = repository.save(value);
        publish("pharmacien.updated.v1", id);
        return saved;
    }

    @PatchMapping("/{id}/account")
    public Pharmacien updateAccount(@PathVariable Long id, @Valid @RequestBody UserAccountPatch patch) {
        Pharmacien value = find(id);
        value.getUser().apply(patch);
        value.getUser().setRole("ROLE_PHARMACIEN");
        Pharmacien saved = repository.save(value);
        publish("pharmacien.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        publish("pharmacien.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private Pharmacien find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pharmacien not found"));
    }

    private void prepare(Pharmacien value) {
        value.getUser().setPassword(PasswordHashing.hash(value.getUser().getPassword()));
        value.getUser().setRole("ROLE_PHARMACIEN");
        value.getUser().initializeTimestamps();
        OffsetDateTime now = OffsetDateTime.now();
        if (value.getCreatedAt() == null) {
            value.setCreatedAt(now);
        }
        value.setUpdatedAt(now);
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("pharmacienId", id)));
    }
}
