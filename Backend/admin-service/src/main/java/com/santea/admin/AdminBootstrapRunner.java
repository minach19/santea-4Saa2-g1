package com.santea.admin;

import com.santea.shared.model.PasswordHashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {
    private final AdminUserRepository repository;
    private final AdminSequenceService sequenceService;
    private final String username;
    private final String email;
    private final String password;

    public AdminBootstrapRunner(
            AdminUserRepository repository,
            AdminSequenceService sequenceService,
            @Value("${santea.bootstrap-admin.username:}") String username,
            @Value("${santea.bootstrap-admin.email:}") String email,
            @Value("${santea.bootstrap-admin.password:}") String password) {
        this.repository = repository;
        this.sequenceService = sequenceService;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            throw new IllegalStateException(
                    "No admin account exists. Set ADMIN_BOOTSTRAP_USERNAME, ADMIN_BOOTSTRAP_EMAIL, and ADMIN_BOOTSTRAP_PASSWORD.");
        }

        AdminUser admin = new AdminUser();
        admin.setId(sequenceService.nextId());
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(PasswordHashing.hash(password));
        admin.setRole("ROLE_ADMIN");
        admin.initializeTimestamps();
        repository.save(admin);
    }
}
