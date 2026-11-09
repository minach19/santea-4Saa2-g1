package com.santea.nutrition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"com.santea.nutrition", "com.santea.shared.events"})
public class NutritionnisteApplication { public static void main(String[] args) { SpringApplication.run(NutritionnisteApplication.class, args); } }
