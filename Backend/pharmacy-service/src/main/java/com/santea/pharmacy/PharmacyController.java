package com.santea.pharmacy;

import com.santea.shared.events.DomainEvent;
import com.santea.shared.events.RabbitEventPublisher;
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
@RequestMapping("/api/v1/pharmacies")
public class PharmacyController {
    private final PharmacyRepository repository;
    private final RabbitEventPublisher events;

    public PharmacyController(PharmacyRepository repository, RabbitEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<Pharmacy> create(@Valid @RequestBody Pharmacy pharmacy) {
        prepare(pharmacy);
        Pharmacy saved = repository.save(pharmacy);
        publish("pharmacy.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/pharmacies/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Pharmacy> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Pharmacy get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public Pharmacy update(@PathVariable Long id, @Valid @RequestBody Pharmacy pharmacy) {
        Pharmacy existing = find(id);
        prepare(pharmacy);
        pharmacy.setId(id);
        pharmacy.setCreatedAt(existing.getCreatedAt());
        pharmacy.setUpdatedAt(OffsetDateTime.now());
        Pharmacy saved = repository.save(pharmacy);
        publish("pharmacy.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        publish("pharmacy.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private Pharmacy find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pharmacy not found"));
    }

    private void prepare(Pharmacy pharmacy) {
        OffsetDateTime now = OffsetDateTime.now();
        if (pharmacy.getCreatedAt() == null) {
            pharmacy.setCreatedAt(now);
        }
        pharmacy.setUpdatedAt(now);
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("pharmacyId", id)));
    }
}
