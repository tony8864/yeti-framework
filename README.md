# YetiFramework

🚀 **YetiFramework** is a lightweight Java framework for **scenario-based API testing**.  
It lets you describe real workflows like:

create user → fetch user → update user → delete user

Readable, reusable, and CI-friendly.

👉 Full documentation: [yetiframework.com](https://yetiframework.com)

---

## ✨ Features

- Define reusable **endpoints** and **test data**
- Build scenarios as ordered **steps**
- Use placeholders like `${userId}` to share values across steps
- Extract values from responses using **JSONPath**
- Pluggable execution:
    - **HttpExecutor** (default via Spring WebClient)
    - Extendable to other transports (gRPC, Kafka, etc.)
- Runs locally or in CI/CD pipelines

---

## 📦 Installation

YetiFramework is available on Maven Central 🎉.
Right now, the framework is split into three modules — include the ones you need:

### Maven

```xml
<dependency>
  <groupId>io.github.tony8864</groupId>
  <artifactId>domain</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>io.github.tony8864</groupId>
  <artifactId>application</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>io.github.tony8864</groupId>
  <artifactId>infrastructure-http</artifactId>
  <version>1.0.0</version>
</dependency>

👉 In a future release, a single “core” dependency will be provided to pull everything in at once.
```

🏃 Quickstart Example

```java
EndpointRegistry.register(
    Endpoint.of("REGISTER_USER", HttpMethod.POST, "/api/users",
        RegisterUserRequest.class, UserResponse.class));

DataRegistry.register("Alice", new RegisterUserRequest("Alice", "alice@email.com", "pass123"));

Scenario scenario = Scenario.of("Register user")
    .step(Step.of("Register user step", "REGISTER_USER", "Alice")
        .saveAs("userId", "$.id")
        .expectStatus(201));

StepRunner runner = new HttpStepRunner(new WebClientHttpExecutor("http://localhost:8080"));
ScenarioExecutor executor = new ScenarioExecutor(runner);

executor.run(scenario);
```