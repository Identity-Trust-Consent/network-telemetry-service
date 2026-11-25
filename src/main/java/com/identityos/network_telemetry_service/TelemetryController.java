package com.identityos.network_telemetry_service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Trust-service calls this server-side endpoint during an evaluation. Demo mode is for local
 * integration verification only: it does not claim to perform live device, IP/VPN, geolocation,
 * or behavioural-analysis checks.
 */
@RestController
public class TelemetryController {
  private static final List<String> TRUST_SIGNALS = List.of(
      "DEVICE_RECOGNIZED", "DEVICE_POSTURE", "DEVICE_SESSION_CONTINUITY",
      "NETWORK_REPUTATION", "APPROVED_NETWORK", "LOCATION_CHECK",
      "IMPOSSIBLE_TRAVEL_CLEAR", "ANONYMOUS_NETWORK_CLEAR",
      "ACTIVITY_CHECK", "BASELINE_CHECK");
  private static final List<String> INCIDENT_SIGNALS = List.of(
      "COMPROMISE_CLEAR", "INCIDENTS_CLEAR", "ACCOUNT_TAKEOVER_CLEAR", "POLICY_VIOLATIONS_CLEAR");
  private final TelemetryProperties properties;

  public TelemetryController(TelemetryProperties properties) { this.properties = properties; }

  @PostMapping("/api/v1/telemetry/trust-evidence")
  public EvidenceResponse evidence(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
      @Valid @RequestBody EvaluationContext context) {
    authenticate(authorization);
    return response(context, TRUST_SIGNALS);
  }

  @PostMapping("/api/v1/telemetry/incident-evidence")
  public EvidenceResponse incidentEvidence(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
      @Valid @RequestBody EvaluationContext context) {
    authenticate(authorization);
    return response(context, INCIDENT_SIGNALS);
  }

  private EvidenceResponse response(EvaluationContext context, List<String> types) {
    String outcome = properties.demoMode() ? "SUCCESS" : "UNAVAILABLE";
    OffsetDateTime observedAt = OffsetDateTime.now();
    return new EvidenceResponse(context.organizationId(), context.applicationId(), context.subjectId(), context.sessionId(),
        types.stream().map(type -> new Signal(type, outcome, observedAt)).toList());
  }

  private void authenticate(String authorization) {
    String expected = properties.evidenceToken();
    if (expected == null || expected.isBlank() || authorization == null
        || !authorization.equals("Bearer " + expected)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Valid telemetry evidence token required");
    }
  }

  public record EvaluationContext(@NotBlank String subjectType, @NotBlank String subjectId,
      @NotBlank String organizationId, @NotBlank String applicationId, String sessionId,
      Map<String, Object> context) {}
  public record Signal(String signalType, String outcome, OffsetDateTime occurredAt) {}
  public record EvidenceResponse(String organizationId, String applicationId, String subjectId,
      String sessionId, List<Signal> signals) {}
}
