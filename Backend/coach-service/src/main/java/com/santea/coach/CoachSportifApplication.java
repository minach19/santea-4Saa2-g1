package com.santea.coach;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"com.santea.coach", "com.santea.shared.events"})
public class CoachSportifApplication { public static void main(String[] args) { SpringApplication.run(CoachSportifApplication.class, args); } }
