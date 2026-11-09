package com.santea.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "santea.user-directory.services")
public class UserDirectoryProperties {
    private String patient;
    private String medecin;
    private String pharmacien;
    private String nutritionniste;
    private String coach;
    private String admin;

    public String getPatient() { return patient; }
    public void setPatient(String patient) { this.patient = patient; }
    public String getMedecin() { return medecin; }
    public void setMedecin(String medecin) { this.medecin = medecin; }
    public String getPharmacien() { return pharmacien; }
    public void setPharmacien(String pharmacien) { this.pharmacien = pharmacien; }
    public String getNutritionniste() { return nutritionniste; }
    public void setNutritionniste(String nutritionniste) { this.nutritionniste = nutritionniste; }
    public String getCoach() { return coach; }
    public void setCoach(String coach) { this.coach = coach; }
    public String getAdmin() { return admin; }
    public void setAdmin(String admin) { this.admin = admin; }
}
