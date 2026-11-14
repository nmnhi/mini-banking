# Mini Banking

Practice project: a small banking system built step by step, from Java core to microservices on AWS.

## Planned modules

| Module | Phase | Purpose |
|---|---|---|
| `account-service` | 1–2 | Accounts, balance, deposit/withdraw |
| `common` | 2 | Shared error handling, security, web |
| `transfer-service` | 3 | Money transfer, idempotency, saga |
| `notification-service` | 3 | Consumes transfer events from Kafka |
| `deploy/` | 3–4 | docker-compose, Kubernetes, Terraform |
| `.github/workflows/` | 4 | CI pipeline |

## Build

```bash
./mvnw verify
```
