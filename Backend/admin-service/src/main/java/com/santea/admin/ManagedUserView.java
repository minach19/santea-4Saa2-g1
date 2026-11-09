package com.santea.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record ManagedUserView(
        String id,
        String profileType,
        Long profileId,
        Long userId,
        String username,
        String email,
        String nom,
        String prenom,
        String role,
        boolean active,
        JsonNode profile
) {}
