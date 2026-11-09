package com.santea.medecin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.santea.shared.model.UserAccount;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medecins")
@Getter
@Setter
@NoArgsConstructor
public class Medecin {
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

    @Column(length = 100)
    private String specialite;

    @Size(max = 50)
    @Column(length = 50, unique = true)
    private String numeroOrdre;

    @Column(length = 255)
    private String cabinetAdresse;

    @Column(length = 100)
    private String cabinetVille;

    @Column(precision = 10, scale = 6)
    private BigDecimal cabinetLat;

    @Column(precision = 10, scale = 6)
    private BigDecimal cabinetLng;

    @Column(length = 20)
    private String telephoneCabinet;

    @Column(precision = 10, scale = 2)
    private BigDecimal tarifConsultation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "medecin_disponibilite_ids", joinColumns = @JoinColumn(name = "medecin_id"))
    private List<Long> disponibilites = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "medecin_rendez_vous_ids", joinColumns = @JoinColumn(name = "medecin_id"))
    private List<Long> rendezVous = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "medecin_rapport_analyse_ids", joinColumns = @JoinColumn(name = "medecin_id"))
    private List<Long> rapportsAnalyses = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "medecin_rapport_medical_ids", joinColumns = @JoinColumn(name = "medecin_id"))
    private List<Long> rapportsMedicaux = new ArrayList<>();
}
