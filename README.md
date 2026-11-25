# Network telemetry service (local demo)

This is a local evidence provider for Device Trust, Network and Location Trust, Behaviour and Activity Trust, and Security Risk and Incident History controls. It implements the
server-to-server response contract consumed by `trust-service`.

It is deliberately not a production IP-reputation, VPN, geolocation, or Tor-detection solution. With
`TELEMETRY_DEMO_MODE=true`, it returns current `SUCCESS` results solely to verify the trust integration.
With demo mode off, it safely returns `UNAVAILABLE`.

## Run locally

In one PowerShell terminal:

```powershell
$env:TELEMETRY_DEMO_MODE = 'true'
$env:TELEMETRY_EVIDENCE_TOKEN = 'replace-with-a-long-local-random-token'
cd network-telemetry-service
mvn spring-boot:run
```

In the terminal that starts `trust-service`, set the same token and provider URL before starting it:

```powershell
$env:TRUST_TELEMETRY_EVIDENCE_URL = 'http://localhost:8090/api/v1/telemetry/trust-evidence'
$env:TRUST_TELEMETRY_EVIDENCE_TOKEN = 'replace-with-a-long-local-random-token'
$env:TRUST_INCIDENTS_EVIDENCE_URL = 'http://localhost:8090/api/v1/telemetry/incident-evidence'
$env:TRUST_INCIDENTS_EVIDENCE_TOKEN = 'replace-with-a-long-local-random-token'
cd trust-service
mvn spring-boot:run
```

Then select **qwerty** and click **Re-evaluate trust**. Device Trust should show `15 / 15`, Network and Location Trust should show `10 / 10`, Behaviour and Activity Trust should show `15 / 15`, and Security Risk and Incident History should show `10 / 10`.

For production, replace demo mode with real provider adapters. Do not treat demo `SUCCESS` signals as a real
security assertion.
