package com.example.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

//@SpringBootApplication(scanBasePackages = { "com.example", "com.example.shop.controller", "com.example.shop.core", "com.example.shop.core", "com.example.shop.database"})
@SpringBootApplication(scanBasePackages = { "com.example.shop", "com.example.demo"})
@OpenAPIDefinition(info = @Info(title = "Shop API", version = "3.0", description = "Shop backend from demo example"))
public class MultipleCoreImplemSampleApplication {

  public static void main(String[] args) {

    // Add accept-single-value-as-array setting to allow single update line in stock.
    SpringApplication application = new SpringApplication(MultipleCoreImplemSampleApplication.class);

    Properties properties = new Properties();
    properties.put("spring.jackson.deserialization.accept-single-value-as-array", "true");
    application.setDefaultProperties(properties);

    application.run(args);
  }

}
