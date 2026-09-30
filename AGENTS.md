# Library Management System - AGENTS.md

## Project Status
**Active Spring Boot Project** - Java 21, Spring Boot 3.2, Maven, MySQL

## Current Structure
```
librarymanagementsystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/library/management/
│   │   │       ├── config/          # Security, OpenAPI config
│   │   │       ├── controller/      # REST Controllers (7)
│   │   │       ├── dto/             # DTOs (20+)
│   │   │       ├── entity/          # JPA Entities (12)
│   │   │       ├── exception/       # Custom Exceptions (4)
│   │   │       ├── repository/      # Spring Data Repositories (7)
│   │   │       ├── service/         # Service Interfaces (7)
│   │   │       │   └── impl/        # Service Implementations (7)
│   │   │       └── LibraryManagementSystemApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/library/management/
│               ├── LibraryManagementSystemApplicationTests.java
│               └── service/
│                   └── UserServiceImplTest.java
├── pom.xml
├── README.md
├── .gitignore
└── AGENTS.md
```

## Technology Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.2.5
- **Build Tool**: Maven
- **Database**: MySQL 8.0 (Testcontainers for testing)
- **ORM**: Spring Data JPA / Hibernate
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Security**: Spring Security (Basic Auth)

## Core Entities
- User (Member) - with roles: ADMIN, LIBRARIAN, MEMBER
- Book - with ISBN, author, category, availability
- Author
- Category
- Borrow/Loan - with due dates, overdue tracking
- Return - with condition tracking
- Fine - with payment/waiver workflow

## Key Commands

### Build & Run
```bash
mvn clean compile           # Compile only
mvn test                    # Run tests (uses Testcontainers)
mvn package                 # Build JAR
mvn spring-boot:run         # Run application
mvn spring-boot:run -Dspring-boot.run.profiles=dev  # Run with profile
```

### Database
```bash
# Application auto-creates schema (ddl-auto=update)
# For fresh schema: mvn spring-boot:run -Dspring.jpa.hibernate.ddl-auto=create
```

### Testing
```bash
mvn test                              # All tests
mvn test -Dtest=UserServiceImplTest   # Specific test class
mvn test -Dtest=*Controller*          # Controller tests (if added)
```

### Development
```bash
mvn clean install -DskipTests  # Build without tests
```

## API Endpoints
Base path: `/api`

- **Users**: `/users` (CRUD + search, status/role management)
- **Books**: `/books` (CRUD + search, filter by author/category/status)
- **Authors**: `/authors` (CRUD + search)
- **Categories**: `/categories` (CRUD + search)
- **Borrows**: `/borrows` (borrow, return, mark lost, overdue tracking)
- **Returns**: `/returns` (create, list)
- **Fines**: `/fines` (view, pay, waive, generate)

## Documentation
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api/v3/api-docs`
- Actuator Health: `http://localhost:8080/api/actuator/health`

## Configuration
Key settings in `src/main/resources/application.properties`:
- Database connection (MySQL)
- JPA/Hibernate settings
- Server port (8080) and context path (/api)
- Logging levels

## Notes for Future Agents
- All services use constructor injection (@RequiredArgsConstructor)
- Global exception handling via @RestControllerAdvice
- Validation via Jakarta Bean Validation annotations
- Pagination via Spring Data Pageable
- Testcontainers used for integration tests (requires Docker)
- Security is basic auth; JWT implementation pending (Milestone 6)
- No flyway/liquibase migrations yet - uses Hibernate ddl-auto

## Fixed Issues (Build Verification)
- **Missing Spring Security dependency**: Added `spring-boot-starter-security` to pom.xml
- **Lombok annotation processing**: Configured maven-compiler-plugin with annotationProcessorPaths
- **Duplicate repository method**: Removed duplicate `findByBorrowId` in ReturnRepository
- **Controller generic type mismatch**: Fixed FineController.generateFinesForOverdueBooks() return type from `ApiResponse<Void>` to `ApiResponse<String>`
- **ApiResponse missing methods**: Added `error(String, T)` and `success(String)` methods to ApiResponse.java
- **GlobalExceptionHandler**: Fixed validation error handling to use new `error(String, Map)` method
- **Controller delete methods**: Fixed 4 controllers (Category, Book, User, Author) to use `ApiResponse.success(String)` returning `ApiResponse<Void>`
- **FineController.generateFinesForOverdueBooks**: Fixed return type from `ApiResponse<String>` to `ApiResponse<Void>`
- **ReturnServiceImpl**: Added missing `Book` entity import

## Verification Results (2026-09-30)
- **Maven version**: Apache Maven 3.9.9
- **Java version**: 21.0.12 (Oracle Corporation)
- **Compilation**: BUILD SUCCESS (65 source files compiled)
- **Unit tests**: 8 tests run, 0 failures, 0 errors, 0 skipped (UserServiceImplTest)
- **Integration tests**: Require Docker for Testcontainers (environment limitation)
- **Lombok warnings**: 13 @Builder initializer warnings (non-blocking, documented for cleanup pass)
- **Final status**: BUILD SUCCESS