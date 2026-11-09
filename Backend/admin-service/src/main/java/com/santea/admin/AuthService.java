package com.santea.admin;

import com.santea.shared.security.JwtProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {
    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public AuthService(
            AdminUserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public AuthResponse login(AuthRequest request) {
        AdminUser admin = repository.findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(
                        request.identifier(), request.identifier())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .filter(user -> !user.isBanned() && user.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Identifiants invalides"));

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getExpiration());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("santea")
                .subject(admin.getUsername())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", List.of("ROLE_ADMIN"))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new AuthResponse(token, "Bearer", jwtProperties.getExpiration().toSeconds(), admin.getUsername());
    }
}
