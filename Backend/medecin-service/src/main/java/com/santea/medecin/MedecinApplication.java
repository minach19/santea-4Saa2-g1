package com.santea.medecin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"com.santea.medecin", "com.santea.shared.events"})
public class MedecinApplication { public static void main(String[] args) { SpringApplication.run(MedecinApplication.class, args); } }
