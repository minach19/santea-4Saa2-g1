package com.santea.pharmacy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"com.santea.pharmacy", "com.santea.shared.events"})
public class PharmacyApplication { public static void main(String[] args) { SpringApplication.run(PharmacyApplication.class, args); } }
