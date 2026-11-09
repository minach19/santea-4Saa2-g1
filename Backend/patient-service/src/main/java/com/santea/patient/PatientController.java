package com.santea.patient;

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
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientRepository repository;
    private final RabbitEventPublisher events;

    public PatientController(PatientRepository repository, RabbitEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<Patient> create(@Valid @RequestBody Patient patient) {
        prepare(patient);
        Patient saved = repository.save(patient);
        publish("patient.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/patients/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Patient> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        Patient existing = find(id);
        prepare(patient);
        patient.getUser().setCreatedAt(existing.getUser().getCreatedAt());
        patient.getUser().initializeTimestamps();
        patient.setId(id);
        patient.setCreatedAt(existing.getCreatedAt());
        patient.setUpdatedAt(OffsetDateTime.now());
        Patient saved = repository.save(patient);
        publish("patient.updated.v1", id);
        return saved;
    }

    @PatchMapping("/{id}/account")
    public Patient updateAccount(@PathVariable Long id, @Valid @RequestBody UserAccountPatch patch) {
        Patient patient = find(id);
        patient.getUser().apply(patch);
        patient.getUser().setRole("ROLE_PATIENT");
        Patient saved = repository.save(patient);
        publish("patient.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        publish("patient.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private Patient find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
    }

    private void prepare(Patient patient) {
        patient.getUser().setPassword(PasswordHashing.hash(patient.getUser().getPassword()));
        patient.getUser().setRole("ROLE_PATIENT");
        patient.getUser().initializeTimestamps();
        OffsetDateTime now = OffsetDateTime.now();
        if (patient.getCreatedAt() == null) {
            patient.setCreatedAt(now);
        }
        patient.setUpdatedAt(now);
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("patientId", id)));
    }
}
