package com.identityos.network_telemetry_service;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"telemetry.demo-mode=true", "telemetry.evidence-token=test-token"})
class TelemetryControllerTests {
  @Autowired MockMvc mvc;
  private static final String BODY = """
      {"subjectType":"USER","subjectId":"user-1","organizationId":"org-1","applicationId":"app-1","sessionId":null,"context":{}}
      """;

  @Test void demoModeSuppliesAllTrustSignals() throws Exception {
    mvc.perform(post("/api/v1/telemetry/trust-evidence").header("Authorization", "Bearer test-token")
        .contentType(MediaType.APPLICATION_JSON).content(BODY))
      .andExpect(status().isOk()).andExpect(jsonPath("$.signals", hasSize(10)))
      .andExpect(jsonPath("$.signals[*].signalType", hasItems(
          "DEVICE_RECOGNIZED", "DEVICE_POSTURE", "DEVICE_SESSION_CONTINUITY",
          "NETWORK_REPUTATION", "APPROVED_NETWORK", "LOCATION_CHECK",
          "IMPOSSIBLE_TRAVEL_CLEAR", "ANONYMOUS_NETWORK_CLEAR",
          "ACTIVITY_CHECK", "BASELINE_CHECK")))
      .andExpect(jsonPath("$.signals[*].outcome", everyItem(is("SUCCESS"))));
  }

  @Test void rejectsRequestsWithoutTheSharedToken() throws Exception {
    mvc.perform(post("/api/v1/telemetry/trust-evidence").contentType(MediaType.APPLICATION_JSON).content(BODY))
      .andExpect(status().isUnauthorized());
  }

  @Test void demoModeSuppliesAllIncidentClearanceSignals() throws Exception {
    mvc.perform(post("/api/v1/telemetry/incident-evidence").header("Authorization", "Bearer test-token")
        .contentType(MediaType.APPLICATION_JSON).content(BODY))
      .andExpect(status().isOk()).andExpect(jsonPath("$.signals", hasSize(4)))
      .andExpect(jsonPath("$.signals[*].signalType", hasItems("COMPROMISE_CLEAR", "INCIDENTS_CLEAR", "ACCOUNT_TAKEOVER_CLEAR", "POLICY_VIOLATIONS_CLEAR")))
      .andExpect(jsonPath("$.signals[*].outcome", everyItem(is("SUCCESS"))));
  }
}
