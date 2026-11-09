package com.santea.pharmacy;

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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pharmaciens")
@Getter
@Setter
@NoArgsConstructor
public class Pharmacien {
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

    @Column(length = 50, unique = true)
    private String numeroOrdre;

    @Column(length = 150)
    private String pharmacieNom;

    @Column(length = 255)
    private String pharmacieAdresse;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "pharmacien_pharmacy_ids", joinColumns = @JoinColumn(name = "pharmacien_id"))
    private List<Long> pharmacies = new ArrayList<>();
}
