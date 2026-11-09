package com.santea.admin;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, String username) {}
