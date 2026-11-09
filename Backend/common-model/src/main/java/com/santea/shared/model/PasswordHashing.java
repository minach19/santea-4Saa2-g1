package com.santea.shared.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordHashing {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordHashing() {}

    public static String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }
}
