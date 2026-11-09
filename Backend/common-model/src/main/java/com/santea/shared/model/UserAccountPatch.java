package com.santea.shared.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAccountPatch {
    @Size(min = 3, max = 180)
    @Pattern(regexp = "[a-zA-Z0-9_.-]+")
    private String username;

    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 2, max = 100)
    private String nom;

    @Size(min = 2, max = 100)
    private String prenom;

    @Size(max = 255)
    private String adresse;

    @Size(max = 20)
    private String telephone;

    @Size(max = 5)
    private String locale;

    private Boolean emailVerified;
    private Boolean adminApproved;
    private Boolean active;

    @AssertTrue(message = "Au moins un champ doit être fourni")
    public boolean isNotEmpty() {
        return username != null || email != null || nom != null || prenom != null
                || adresse != null || telephone != null || locale != null
                || emailVerified != null || adminApproved != null || active != null;
    }
}
