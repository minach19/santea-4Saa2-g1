package com.santea.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {
    @NotBlank
    @Size(min = 3, max = 180)
    @Pattern(regexp = "[a-zA-Z0-9_.-]+")
    @Column(length = 180, unique = true)
    private String username;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(length = 255, unique = true)
    private String email;

    @NotBlank
    @Size(max = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 255, nullable = false)
    private String password;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(length = 100)
    private String nom;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(length = 100)
    private String prenom;

    private LocalDate dateNaissance;

    @Column(length = 255)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @NotBlank
    @Pattern(regexp = "ROLE_USER|ROLE_PATIENT|ROLE_MEDECIN|ROLE_PHARMACIEN|ROLE_COACH|ROLE_NUTRITIONNISTE|ROLE_ADMIN")
    @Column(length = 50, nullable = false)
    private String role = "ROLE_USER";

    private boolean emailVerified;
    private boolean adminApproved;
    private boolean mfaEnabled;

    @JsonIgnore
    @Column(length = 255)
    private String googleAuthenticatorSecret;

    @Column(length = 10)
    private String themePreference = "light";

    @Column(length = 5)
    private String locale = "fr";

    @Column(columnDefinition = "text")
    private String avatarData;

    @Column(length = 50)
    private String avatarMime;

    private boolean reminderEnabled = true;

    @JsonIgnore
    @Column(length = 255)
    private String emailVerificationToken;

    private OffsetDateTime emailVerificationExpiresAt;

    @Column(length = 20)
    private String subscriptionStatus = "PENDING";

    @Column(length = 50)
    private String subscriptionType;

    private OffsetDateTime subscriptionEndAt;
    private OffsetDateTime aiFreeMonthGrantedAt;

    @JsonProperty("isBanned")
    private boolean isBanned;
    @Column(length = 255)
    private String banReason;
    private OffsetDateTime banUntil;
    private OffsetDateTime deletedAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime updatedAt;

    private Long patientId;
    private Long medecinId;
    private Long pharmacienId;
    private Long coachSportifId;
    private Long nutritionnisteId;
    private Long googleFitAccountId;

    @ElementCollection
    @CollectionTable(name = "user_contenu_ids", joinColumns = @JoinColumn(name = "user_owner_id"))
    private List<Long> contenus = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_like_ids", joinColumns = @JoinColumn(name = "user_owner_id"))
    private List<Long> likes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_commentaire_ids", joinColumns = @JoinColumn(name = "user_owner_id"))
    private List<Long> commentaires = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_reponse_medicament_ids", joinColumns = @JoinColumn(name = "user_owner_id"))
    private List<Long> reponsesMedicaments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_notification_ids", joinColumns = @JoinColumn(name = "user_owner_id"))
    private List<Long> notifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_sante_quotidienne_ids", joinColumns = @JoinColumn(name = "user_owner_id"))
    private List<Long> santeQuotidiennes = new ArrayList<>();

    public void initializeTimestamps() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    public void apply(UserAccountPatch patch) {
        if (patch.getUsername() != null) username = patch.getUsername();
        if (patch.getEmail() != null) email = patch.getEmail();
        if (patch.getNom() != null) nom = patch.getNom();
        if (patch.getPrenom() != null) prenom = patch.getPrenom();
        if (patch.getAdresse() != null) adresse = patch.getAdresse();
        if (patch.getTelephone() != null) telephone = patch.getTelephone();
        if (patch.getLocale() != null) locale = patch.getLocale();
        if (patch.getEmailVerified() != null) emailVerified = patch.getEmailVerified();
        if (patch.getAdminApproved() != null) adminApproved = patch.getAdminApproved();
        if (patch.getActive() != null) {
            setBanned(!patch.getActive());
            if (patch.getActive()) deletedAt = null;
        }
        initializeTimestamps();
    }
}
