package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.example",  "com.example.shop.core", "com.example.shop.core", "com.example.shop.database"})
public class MultipleCoreImplemSampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(MultipleCoreImplemSampleApplication.class, args);
  }

}
