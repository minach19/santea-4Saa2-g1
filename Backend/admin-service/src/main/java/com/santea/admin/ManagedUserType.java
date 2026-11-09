package com.santea.admin;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public enum ManagedUserType {
    PATIENT("patient", "patients", "ROLE_PATIENT"),
    MEDECIN("medecin", "medecins", "ROLE_MEDECIN"),
    PHARMACIEN("pharmacien", "pharmaciens", "ROLE_PHARMACIEN"),
    NUTRITIONNISTE("nutritionniste", "nutritionnistes", "ROLE_NUTRITIONNISTE"),
    COACH("coach-sportif", "sports-coaches", "ROLE_COACH"),
    ADMIN("admin", "admin-users", "ROLE_ADMIN");

    private final String key;
    private final String collection;
    private final String role;

    ManagedUserType(String key, String collection, String role) {
        this.key = key;
        this.collection = collection;
        this.role = role;
    }

    public String key() { return key; }
    public String collection() { return collection; }
    public String role() { return role; }

    public static ManagedUserType fromKey(String key) {
        for (ManagedUserType type : values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new ResponseStatusException(BAD_REQUEST, "Type de profil inconnu");
    }

    public String serviceUrl(UserDirectoryProperties properties) {
        return switch (this) {
            case PATIENT -> properties.getPatient();
            case MEDECIN -> properties.getMedecin();
            case PHARMACIEN -> properties.getPharmacien();
            case NUTRITIONNISTE -> properties.getNutritionniste();
            case COACH -> properties.getCoach();
            case ADMIN -> properties.getAdmin();
        };
    }
}
