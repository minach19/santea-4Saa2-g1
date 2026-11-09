package com.santea.pharmacy;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pharmacies")
@Getter
@Setter
@NoArgsConstructor
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Long pharmacienId;

    @NotBlank
    @Column(length = 150)
    private String nom;

    @NotBlank
    @Column(length = 255)
    private String adresse;

    @Column(length = 20, unique = true)
    private String telephone;

    @Email
    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 255)
    private String horaires;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @JsonProperty("isActive")
    private boolean isActive = true;

    @Column(length = 255)
    private String imageName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "pharmacy_stock_ids", joinColumns = @JoinColumn(name = "pharmacy_id"))
    private List<Long> stocks = new ArrayList<>();
}
