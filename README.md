# Espresso Achievement Service

A comprehensive Spring Boot microservice for managing user achievements, skill tracking, and gamification features. This service provides secure APIs for creating, updating, and managing achievement records with rich media support and social features.

## Overview

The Espresso Achievement Service is a production-ready microservice that provides:

- **Achievement Management**: Create, read, update, disable, and delete achievement records
- **Media Support**: Upload and manage multiple media files per achievement (images, videos)
- **Social Features**: Comment system for achievements with user interactions
- **Skill Tracking**: Tag achievements with skills for analytics and filtering
- **Security**: JWT-based authentication with role-based authorization
- **Visibility Controls**: Public/private achievement settings
- **RESTful APIs**: Comprehensive REST endpoints with OpenAPI documentation

## Technology Stack

### Java Spring Framework
This service is built with **Java 21** and the **Spring Boot 3.5.3** ecosystem, leveraging the power of modern Java development.

#### Benefits of Spring Boot:
- **Rapid Development**: Auto-configuration and starter dependencies reduce boilerplate code
- **Production-Ready**: Built-in health checks, metrics, and monitoring capabilities
- **Microservice Architecture**: Perfect for distributed systems and cloud deployments
- **Dependency Injection**: Clean, testable code with inversion of control
- **Data Access**: Simplified database operations with Spring Data JPA
- **Security**: Comprehensive security features with Spring Security
- **Testing**: Excellent testing support with TestContainers and MockMvc

#### Key Spring Technologies Used:
- **Spring Boot**: Application framework and auto-configuration
- **Spring Web**: RESTful web services and MVC architecture
- **Spring Security**: Authentication, authorization, and JWT token handling
- **Spring Data JPA**: Object-relational mapping and database operations
- **Spring Validation**: Request validation and error handling
- **Spring AOP**: Cross-cutting concerns with aspect-oriented programming

#### Learn More About Spring:
- [Spring Boot Official Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework Core Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html)
- [Spring Boot Getting Started Guides](https://spring.io/guides)
- [Spring Academy - Free Learning Platform](https://spring.academy/)

## Folder Structure

```
espresso-achievement/
├── .github/                    # GitHub workflows and templates
├── api-tests/                  # HTTP client test files for API testing
├── infrastructure/             # Helm charts and Kubernetes deployment configs
├── private/                    # Private documentation and configuration files
├── service/                    # Service-level documentation and acceptance criteria
├── src/                        # Source code directory
│   ├── main/java/espresso/     # Main application code
│   │   ├── achievement/        # Achievement module (detailed below)
│   │   ├── common/             # Shared utilities and common components
│   │   ├── security/           # Security configuration and utilities
│   │   ├── user/               # User management module
│   │   └── *.java              # Application configuration classes
│   ├── main/resources/         # Configuration files and static resources
│   └── test/java/              # Unit and integration tests
├── target/                     # Compiled classes and build artifacts
├── Dockerfile                  # Docker container configuration
├── compose.yaml               # Docker Compose for local development
├── pom.xml                    # Maven project configuration
└── README.md                  # This file
```

### Achievement Module Deep Dive

The `src/main/java/espresso/achievement/` module follows a clean architecture pattern:

```
achievement/
├── application/                # Application layer - business logic orchestration
│   ├── handlers/              # Command handlers for business operations
│   └── response/              # Response DTOs and result objects
├── domain/                    # Domain layer - core business logic
│   ├── commands/              # Command objects for operations
│   ├── contracts/             # Interface definitions (ports)
│   ├── entities/              # Domain entities and business objects
│   └── constants/             # Domain constants and enumerations
├── infrastructure/            # Infrastructure layer - external concerns
│   └── repositories/          # Data access implementations
└── service/                   # Service layer - API controllers
    └── acceptanceCriteria/    # Business requirements documentation
```

**Layer Responsibilities:**
- **Service Layer**: HTTP endpoints, request/response handling, API documentation
- **Application Layer**: Business logic coordination, command processing, validation
- **Domain Layer**: Core business rules, entities, and domain logic
- **Infrastructure Layer**: Database access, external integrations, technical concerns

## Architecture

This service implements **Clean Architecture** (also known as Hexagonal Architecture) principles:

### Architecture Benefits:
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Dependency Inversion**: Dependencies point inward toward business logic
- **Testability**: Easy to unit test business logic in isolation
- **Maintainability**: Changes in external concerns don't affect business rules
- **Flexibility**: Easy to swap implementations (database, frameworks, etc.)

### Key Architectural Patterns:
- **Command Pattern**: Operations encapsulated as command objects
- **Repository Pattern**: Data access abstraction with clear contracts
- **Dependency Injection**: Loose coupling through interface-based design
- **Domain-Driven Design**: Rich domain models with business logic encapsulation

### Data Flow:
```
HTTP Request → Service Layer → Application Layer → Domain Layer → Infrastructure Layer → Database
                     ↓              ↓               ↓                    ↓
                API Controllers → Handlers → Entities/Commands → Repositories → JPA/Database
```

## Testing Strategy

Our testing approach follows a **"Acceptance Criteria First"** methodology with comprehensive test coverage:

### 1. Acceptance Criteria-Driven Development
- **Business Requirements First**: Each feature starts with detailed acceptance criteria
- **Behavior Specification**: Clear scenarios defining expected system behavior
- **Stakeholder Alignment**: Business-readable specifications for validation

### 2. Comprehensive Unit Test Coverage
- **Layer-by-Layer Testing**: Each architectural layer thoroughly tested in isolation
- **Domain Logic Focus**: Core business rules extensively validated
- **Mocking Strategy**: External dependencies mocked for fast, reliable tests
- **Edge Case Coverage**: Boundary conditions and error scenarios tested

### 3. Test Pyramid Implementation
- **Unit Tests** (Foundation): Fast, isolated tests for individual components
- **Integration Tests** (Middle): Database and component interaction testing
- **API Tests** (Top): End-to-end HTTP endpoint validation

### 4. Testing Tools & Frameworks
- **JUnit 5**: Modern testing framework with advanced features
- **Mockito**: Mocking framework for dependency isolation
- **Spring Boot Test**: Integration testing with Spring context
- **TestContainers**: Real database testing with Docker containers

## Deployment

### Containerization with Docker

The service is containerized using **Docker** for consistent deployment across environments:

```dockerfile
# Multi-stage build for optimized production images
FROM openjdk:21-jdk AS builder
# ... build application
FROM openjdk:21-jre
# ... runtime configuration
```

**Docker Benefits:**
- **Consistency**: Same environment across development, testing, and production
- **Portability**: Runs anywhere Docker is supported
- **Isolation**: Application dependencies contained
- **Scalability**: Easy horizontal scaling

### Kubernetes Deployment with Helm

The `infrastructure/espresso-service/` directory contains **Helm charts** for Kubernetes deployment:

```
infrastructure/espresso-service/
├── Chart.yaml          # Helm chart metadata
├── values.yaml         # Default configuration values
└── templates/          # Kubernetes resource templates
    ├── deployment.yaml # Application deployment
    ├── service.yaml    # Service definition
    ├── configmap.yaml  # Configuration management
    └── ingress.yaml    # External access configuration
```

**Kubernetes + Helm Benefits:**
- **Orchestration**: Automated deployment, scaling, and management
- **High Availability**: Built-in redundancy and health checking
- **Resource Management**: Efficient CPU and memory allocation
- **Configuration Management**: Environment-specific configurations
- **Rolling Updates**: Zero-downtime deployments

### Learn More About Deployment Technologies:
- [Docker Official Documentation](https://docs.docker.com/)
- [Docker Getting Started Guide](https://docs.docker.com/get-started/)
- [Kubernetes Official Documentation](https://kubernetes.io/docs/home/)
- [Helm Official Documentation](https://helm.sh/docs/)
- [Kubernetes Learning Path](https://kubernetes.io/docs/concepts/)

## Dependencies

### Core Spring Dependencies
| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-web` | 3.5.3 | Web MVC framework and REST APIs |
| `spring-boot-starter-data-jpa` | 3.5.3 | Database access and ORM |
| `spring-boot-starter-security` | 3.5.3 | Authentication and authorization |
| `spring-boot-starter-validation` | 3.5.3 | Request validation and constraints |
| `spring-boot-starter-actuator` | 3.5.3 | Health checks and monitoring |
| `spring-boot-starter-aop` | 3.5.3 | Aspect-oriented programming |

### Database & Persistence
| Dependency | Version | Purpose |
|------------|---------|---------|
| `postgresql` | Runtime | PostgreSQL database driver |
| `jackson-datatype-hibernate5` | Latest | JSON serialization for JPA entities |

### Security & Authentication  
| Dependency | Version | Purpose |
|------------|---------|---------|
| `jjwt-api` | 0.12.6 | JSON Web Token processing |
| `jjwt-impl` | 0.12.6 | JWT implementation |
| `jjwt-jackson` | 0.12.6 | JWT JSON processing |
| `spring-boot-starter-oauth2-client` | 3.5.3 | OAuth2 client support |

### Documentation & API
| Dependency | Version | Purpose |
|------------|---------|---------|
| `springdoc-openapi-starter-webmvc-ui` | 2.6.0 | OpenAPI/Swagger documentation |

### Cloud & External Services
| Dependency | Version | Purpose |
|------------|---------|---------|
| `aws-java-sdk-s3` | 1.12.783 | AWS S3 integration for media storage |
| `azure-ai-textanalytics` | 5.5.4 | Azure AI text analysis services |
| `spring-cloud-azure-dependencies` | 5.18.0 | Azure cloud integration |

### Development & Testing
| Dependency | Version | Purpose |
|------------|---------|---------|
| `lombok` | Latest | Reduce boilerplate code |
| `spring-boot-starter-test` | 3.5.3 | Testing framework and utilities |

## Running The App

### Prerequisites
- **Java 21** or later
- **Maven 3.6+** 
- **PostgreSQL 12+** database
- **Docker** (optional, for containerized database)

### Local Development Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/Achievement-Unlocked-Liberica/espresso-achievement.git
cd espresso-achievement
```

#### 2. Database Setup

**Option A: Using Docker Compose (Recommended)**
```bash
# Start PostgreSQL database in container
docker-compose up -d database
```

**Option B: Local PostgreSQL Installation**
```bash
# Create database
createdb espresso_achievement

# Update src/main/resources/application-lcl.properties with your database settings
```

#### 3. Configure Application Properties
Create or update `src/main/resources/application-lcl.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/espresso_achievement
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000

# AWS S3 Configuration (if using media upload)
aws.s3.bucket=your-s3-bucket-name
aws.s3.region=us-west-2
```

#### 4. Build and Run

**Using Maven Wrapper (Recommended)**
```bash
# Make Maven wrapper executable (Linux/Mac)
chmod +x mvnw

# Build the application
./mvnw clean compile

# Run tests
./mvnw test

# Start the application
./mvnw spring-boot:run
```

**Using System Maven**
```bash
mvn clean compile
mvn test  
mvn spring-boot:run
```

#### 5. Verify Installation
- **Health Check**: http://localhost:8080/actuator/health
- **API Documentation**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Production Deployment

#### Docker Deployment
```bash
# Build Docker image
docker build -t espresso-achievement:latest .

# Run with Docker Compose
docker-compose up -d
```

#### Kubernetes Deployment
```bash
# Deploy using Helm
helm install espresso-achievement ./infrastructure/espresso-service/

# Check deployment status
kubectl get pods -l app=espresso-achievement
```

### Development Tools

#### API Testing
Use the provided HTTP client files in `api-tests/` directory:
- **VS Code**: Install REST Client extension
- **IntelliJ IDEA**: Built-in HTTP client support
- **Postman**: Import OpenAPI specification

#### Database Management
- **pgAdmin**: Web-based PostgreSQL administration
- **DBeaver**: Universal database tool
- **DataGrip**: JetBrains database IDE

### Troubleshooting

#### Common Issues:
1. **Port 8080 already in use**: Change port in `application.properties`
2. **Database connection failed**: Verify PostgreSQL is running and credentials are correct
3. **JWT errors**: Ensure secret key is properly configured
4. **Media upload fails**: Check AWS S3 credentials and bucket permissions

#### Logs and Monitoring:
- Application logs: `logs/application.log`
- Health endpoint: `/actuator/health`
- Metrics endpoint: `/actuator/metrics`

---

## Contributing

Please read our contributing guidelines and ensure all tests pass before submitting pull requests. This service follows clean architecture principles and comprehensive testing practices.