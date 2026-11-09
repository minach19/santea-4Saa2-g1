package com.santea.admin;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminUserRepository extends MongoRepository<AdminUser, Long> {
    Optional<AdminUser> findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
}
