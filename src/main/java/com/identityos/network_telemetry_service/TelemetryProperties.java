package com.identityos.network_telemetry_service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telemetry")
public record TelemetryProperties(boolean demoMode, String evidenceToken) {}
