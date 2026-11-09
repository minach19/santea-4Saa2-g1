package com.santea.patient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.santea.shared.model.UserAccount;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private Long userId;

    @NotNull
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "createdAt", column = @Column(name = "user_created_at")),
            @AttributeOverride(name = "updatedAt", column = @Column(name = "user_updated_at"))
    })
    private UserAccount user;

    @Size(max = 50)
    @Column(length = 50, unique = true)
    private String numeroSecu;

    @Size(max = 5)
    @Column(length = 5)
    private String groupeSanguin;

    private String allergies;
    private String antecedentsMedicaux;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "patient_rendez_vous_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> rendezVous = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patient_rapport_analyse_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> rapportsAnalyses = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patient_rapport_medical_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> rapportsMedicaux = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patient_journal_item_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> journalItems = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patient_symptome_quotidien_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> symptomesQuotidiens = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patient_plan_exercice_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> plansExercices = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patient_plan_regime_ids", joinColumns = @JoinColumn(name = "patient_id"))
    private List<Long> plansRegimes = new ArrayList<>();
}
