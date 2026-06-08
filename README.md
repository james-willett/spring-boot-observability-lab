# Spring Boot 4 Observability Lab

The runtime application for the Pluralsight Cloud Lab
**"Guided: Configuring and Monitoring a Spring Boot 4 Application in the Cloud."**

A deliberately minimal Spring Boot 4 web app that ships as a pre-built JAR. Learners
make it observable using **configuration only** — editing `application.properties` to
enable Actuator, expose health probes, and turn on distributed tracing. No Java changes.

## Endpoints

| Endpoint | Purpose |
|----------|---------|
| `GET /hello` | Fast happy-path request (single span) |
| `GET /slow`  | ~500 ms latency, for timing metrics |
| `GET /chain` | Calls `/hello` internally → multi-span trace |
| `GET /flaky` | Fails ~50% of the time, for error-rate signals |

## Tech stack

- Spring Boot 4.0.6 · Java 25 · Maven
- `spring-boot-starter-actuator` + `spring-boot-starter-zipkin`

## Build

```bash
./mvnw clean package
# JAR -> target/spring-boot-observability-lab-1.0.0.jar
```

The published JAR is attached to the [`v1.0.0` release](../../releases/tag/v1.0.0),
which the lab's CloudFormation template downloads at provision time.
