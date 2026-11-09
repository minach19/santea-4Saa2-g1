package com.santea.medecin;

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
@RequestMapping("/api/v1/medecins")
public class MedecinController {
    private final MedecinRepository repository;
    private final RabbitEventPublisher events;

    public MedecinController(MedecinRepository repository, RabbitEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<Medecin> create(@Valid @RequestBody Medecin medecin) {
        prepare(medecin);
        Medecin saved = repository.save(medecin);
        publish("medecin.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/medecins/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Medecin> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Medecin get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public Medecin update(@PathVariable Long id, @Valid @RequestBody Medecin medecin) {
        Medecin existing = find(id);
        prepare(medecin);
        medecin.getUser().setCreatedAt(existing.getUser().getCreatedAt());
        medecin.getUser().initializeTimestamps();
        medecin.setId(id);
        medecin.setCreatedAt(existing.getCreatedAt());
        medecin.setUpdatedAt(OffsetDateTime.now());
        Medecin saved = repository.save(medecin);
        publish("medecin.updated.v1", id);
        return saved;
    }

    @PatchMapping("/{id}/account")
    public Medecin updateAccount(@PathVariable Long id, @Valid @RequestBody UserAccountPatch patch) {
        Medecin medecin = find(id);
        medecin.getUser().apply(patch);
        medecin.getUser().setRole("ROLE_MEDECIN");
        Medecin saved = repository.save(medecin);
        publish("medecin.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        publish("medecin.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private Medecin find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Medecin not found"));
    }

    private void prepare(Medecin medecin) {
        medecin.getUser().setPassword(PasswordHashing.hash(medecin.getUser().getPassword()));
        medecin.getUser().setRole("ROLE_MEDECIN");
        medecin.getUser().initializeTimestamps();
        OffsetDateTime now = OffsetDateTime.now();
        if (medecin.getCreatedAt() == null) {
            medecin.setCreatedAt(now);
        }
        medecin.setUpdatedAt(now);
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("medecinId", id)));
    }
}
