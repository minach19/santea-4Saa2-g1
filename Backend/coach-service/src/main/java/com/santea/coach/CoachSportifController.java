package com.santea.coach;

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
@RequestMapping("/api/v1/sports-coaches")
public class CoachSportifController {
    private final CoachSportifRepository repository;
    private final RabbitEventPublisher events;

    public CoachSportifController(CoachSportifRepository repository, RabbitEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping
    public ResponseEntity<CoachSportif> create(@Valid @RequestBody CoachSportif value) {
        prepare(value);
        CoachSportif saved = repository.save(value);
        publish("sports-coach.created.v1", saved.getId());
        return ResponseEntity.created(URI.create("/api/v1/sports-coaches/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<CoachSportif> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public CoachSportif get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public CoachSportif update(@PathVariable Long id, @Valid @RequestBody CoachSportif value) {
        CoachSportif existing = find(id);
        prepare(value);
        value.getUser().setCreatedAt(existing.getUser().getCreatedAt());
        value.getUser().initializeTimestamps();
        value.setId(id);
        value.setCreatedAt(existing.getCreatedAt());
        CoachSportif saved = repository.save(value);
        publish("sports-coach.updated.v1", id);
        return saved;
    }

    @PatchMapping("/{id}/account")
    public CoachSportif updateAccount(@PathVariable Long id, @Valid @RequestBody UserAccountPatch patch) {
        CoachSportif value = find(id);
        value.getUser().apply(patch);
        value.getUser().setRole("ROLE_COACH");
        CoachSportif saved = repository.save(value);
        publish("sports-coach.updated.v1", id);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        publish("sports-coach.deleted.v1", id);
        return ResponseEntity.noContent().build();
    }

    private CoachSportif find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Coach sportif not found"));
    }

    private void prepare(CoachSportif value) {
        value.getUser().setPassword(PasswordHashing.hash(value.getUser().getPassword()));
        value.getUser().setRole("ROLE_COACH");
        value.getUser().initializeTimestamps();
        if (value.getCreatedAt() == null) {
            value.setCreatedAt(OffsetDateTime.now());
        }
    }

    private void publish(String type, Long id) {
        events.publish(DomainEvent.of(type, id.toString(), Map.of("coachSportifId", id)));
    }
}
