package com.santea.nutrition;

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
@RequestMapping("/api/v1/nutritionnistes")
public class NutritionnisteController {
    private final NutritionnisteRepository repository;
    private final RabbitEventPublisher events;

    public NutritionnisteController(NutritionnisteRepository repository, RabbitEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<Nutritionniste> create(@Valid @RequestBody Nutritionniste value) {
        prepare(value);
        Nutritionniste saved = repository.save(value);
        publish("nutritionniste.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/nutritionnistes/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Nutritionniste> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Nutritionniste get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public Nutritionniste update(@PathVariable Long id, @Valid @RequestBody Nutritionniste value) {
        Nutritionniste existing = find(id);
        prepare(value);
        value.getUser().setCreatedAt(existing.getUser().getCreatedAt());
        value.getUser().initializeTimestamps();
        value.setId(id);
        value.setCreatedAt(existing.getCreatedAt());
        Nutritionniste saved = repository.save(value);
        publish("nutritionniste.updated.v1", id);
        return saved;
    }

    @PatchMapping("/{id}/account")
    public Nutritionniste updateAccount(@PathVariable Long id, @Valid @RequestBody UserAccountPatch patch) {
        Nutritionniste value = find(id);
        value.getUser().apply(patch);
        value.getUser().setRole("ROLE_NUTRITIONNISTE");
        Nutritionniste saved = repository.save(value);
        publish("nutritionniste.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        publish("nutritionniste.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private Nutritionniste find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Nutritionniste not found"));
    }

    private void prepare(Nutritionniste value) {
        value.getUser().setPassword(PasswordHashing.hash(value.getUser().getPassword()));
        value.getUser().setRole("ROLE_NUTRITIONNISTE");
        value.getUser().initializeTimestamps();
        if (value.getCreatedAt() == null) {
            value.setCreatedAt(OffsetDateTime.now());
        }
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("nutritionnisteId", id)));
    }
}
