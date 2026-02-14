# Currency Exchange System

> A desktop application for managing currency exchange transactions between consultants and clients.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red?logo=apachemaven)](https://maven.apache.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?logo=java)](https://openjfx.io/)
---

## Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
<!-- - [Usage](#usage) -->
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [UML Diagrams](#uml-diagrams)
- [Team](#team)
- [License](#license)

---

## About

The present project is currently in development as part of the **Software Engineering** module of the **Advanced Federal Diploma in Computer Science** programme during the **2025/2026 winter semester**.

The application allows consultants to manage clients and perform currency exchange transactions with real-time validation and state tracking.

---

## Features

- User authentication with role-based access (Admin / Consultant)
- Client management
- Currency exchange transactions with 10 supported currencies
- Input validation for amounts, emails, passwords, and names
- Transaction state tracking (Not Started → Pending → Successful / Failure)

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Maven | Build & dependency management |
| JavaFX 21 | Desktop UI framework |
| JUnit 5.11 | Testing |
| PlantUML | UML documentation |

---

## Prerequisites

Ensure you have the following installed:

- **Java JDK 21+** — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **Git** — [Download](https://git-scm.com/)

Verify your installation:

```bash
java --version
mvn --version
git --version
```

---

## Installation

```bash
# Clone the repository
git clone https://github.com/<username>/<repo-name>.git
cd <repo-name>

# Build all modules from the root directory
mvn clean install

# Run the application (from the presentation module)
mvn javafx:run -pl <artifactId>-presentation
```

---

<!-- TODO
## Usage

1. Launch the application with `mvn javafx:run`
2. Log in with your credentials
3. **Admin:** Manage consultants and clients
4. **Consultant:** Create and manage currency exchange transactions
-->

## Project Structure

The project uses a **multi-module Maven architecture** with Java modules (JPMS). Each architectural layer is isolated in its own module with its own `pom.xml` and `module-info.java`.

```
<project-root>/
├── pom.xml                              # Parent POM (module aggregation & dependency management)
│
├── <artifactId>-domain/                 # Domain layer
│   ├── pom.xml
│   └── src/main/java/
│       ├── module-info.java
│       └── com/<groupId>/domain/
│           ├── entity/                  # User, Client, Transaction
│           ├── valueobject/             # Amount, Name, Password, EmailAddress
│           └── enumeration/             # Role, Currency, TransactionState
│
├── <artifactId>-infrastructure/         # Infrastructure layer
│   ├── pom.xml
│   └── src/main/java/
│       ├── module-info.java
│       └── com/<groupId>/infrastructure/
│
├── <artifactId>-application/            # Application layer
│   ├── pom.xml
│   └── src/main/java/
│       ├── module-info.java
│       └── com/<groupId>/application/
│
├── <artifactId>-presentation/           # Presentation layer (JavaFX UI)
│   ├── pom.xml
│   └── src/main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/<groupId>/presentation/
│       └── resources/
│           ├── fxml/                    # FXML layout files
│           ├── css/                     # Stylesheets
│           └── images/                  # Assets
│
├── docs/
│   └── uml/                             # PlantUML diagrams
└── README.md
```

### Module Dependencies

```
presentation ──→ application ←── infrastructure
                     │
                   domain
```

Each module only depends on the layer directly below it, enforced via `module-info.java` and Maven dependencies.

---

## Architecture

The application follows a **layered architecture**, enforced at build-time through **Maven modules** and at runtime through **Java Platform Module System (JPMS)**.

| Module | Layer | Responsibility | Depends on |
|---|---|---|---|
| `<artifactId>-domain` | **Domain** | Entities, value objects, enums | — |
| `<artifactId>-infrastructure` | **Infrastructure** | Data persistence and external services | application |
| `<artifactId>-presentation` | **Presentation** | JavaFX UI (FXML + Controllers) | application |

---

## UML Diagrams

UML diagrams are located in `docs/uml/` and can be rendered with any PlantUML-compatible tool.

| Diagram | Description | File |
|---|---|---|
| System context overview | Analysis of system context  | [`system_context_overview.puml`](docs/uml/system_context_overview.puml) |
| System context | System context and actors | [`system_context.puml`](docs/uml/system_context.puml) |
| System context | More detailed view on the system | [`system_context_detailed.puml`](docs/uml/system_context_detailed.puml) |
| Use Case | Business use cases | [`business_use_case.puml`](docs/uml/business_use_case.puml) |
| Use Case | System use cases | [`system_use_case.puml`](docs/uml/system_use_case.puml) |
| Class Analysis | High-level domain overview | [`class-analysis.puml`](docs/uml/class_analysis.puml) |
| Domain Layer | Detailed class diagram | [`domain_layer_detail.puml`](docs/uml/domain_layer_detail.puml) |

---