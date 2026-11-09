package com.santea.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.santea.shared.model.UserAccountPatch;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserDirectoryController {
    private final UserDirectoryService directoryService;

    public UserDirectoryController(UserDirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    @Operation(summary = "Lister et rechercher les comptes",
            description = "Filtrer par type de profil, texte libre (nom, prénom, identifiant, e-mail) et état actif.")
    public List<ManagedUserView> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean active,
            JwtAuthenticationToken authentication) {
        return directoryService.list(token(authentication), type, q, active);
    }

    @GetMapping("/{type}/{id}")
    @Operation(summary = "Consulter un compte et son profil métier")
    public ManagedUserView get(
            @PathVariable String type,
            @PathVariable Long id,
            JwtAuthenticationToken authentication) {
        return directoryService.get(token(authentication), type, id);
    }

    @PostMapping("/{type}")
    @Operation(summary = "Créer un compte avec son profil métier",
            description = "Le corps JSON est celui attendu par le service du type de profil sélectionné.")
    public ResponseEntity<ManagedUserView> create(
            @PathVariable String type,
            @RequestBody JsonNode profile,
            JwtAuthenticationToken authentication) {
        ManagedUserView created = directoryService.create(token(authentication), type, profile);
        return ResponseEntity.created(URI.create("/api/v1/users/" + created.profileType() + "/" + created.profileId()))
                .body(created);
    }

    @PatchMapping("/{type}/{id}")
    @Operation(summary = "Modifier les informations communes ou activer/désactiver un compte",
            description = "Utiliser active=true ou active=false pour changer l'état. Le type et le rôle du profil ne peuvent pas être convertis.")
    public ManagedUserView updateAccount(
            @PathVariable String type,
            @PathVariable Long id,
            @Valid @RequestBody UserAccountPatch patch,
            JwtAuthenticationToken authentication) {
        return directoryService.updateAccount(token(authentication), type, id, patch);
    }

    @DeleteMapping("/{type}/{id}")
    @Operation(summary = "Supprimer un compte et son profil métier")
    public ResponseEntity<Void> delete(
            @PathVariable String type,
            @PathVariable Long id,
            JwtAuthenticationToken authentication) {
        directoryService.delete(token(authentication), type, id);
        return ResponseEntity.noContent().build();
    }

    private String token(JwtAuthenticationToken authentication) {
        return authentication.getToken().getTokenValue();
    }
}
