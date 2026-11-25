package com.identityos.network_telemetry_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NetworkTelemetryServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(NetworkTelemetryServiceApplication.class, args);
  }
}
