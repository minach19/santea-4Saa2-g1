package com.santea.admin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"com.santea.admin", "com.santea.shared.events"})
public class AdminUserApplication { public static void main(String[] args) { SpringApplication.run(AdminUserApplication.class, args); } }
