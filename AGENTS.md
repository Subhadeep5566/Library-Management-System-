# Library Management System - AGENTS.md

## Project Status
**Active Spring Boot + JavaFX Project** - Java 21, Spring Boot 3.2.5, Maven, MySQL, JavaFX 21

## Current Structure
```
librarymanagementsystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/library/management/
│   │   │       ├── config/          # Security, OpenAPI config
│   │   │       ├── controller/      # REST Controllers (7)
│   │   │       ├── desktop/         # JavaFX Desktop Application
│   │   │       │   ├── LibraryDesktopApplication.java
│   │   │       │   ├── ui/
│   │   │       │   │   └── login/
│   │   │       │   │       └── LoginView.java
│   │   │       │   └── config/      # Desktop config (future)
│   │   │       ├── dto/             # DTOs (20+)
│   │   │       ├── entity/          # JPA Entities (12)
│   │   │       ├── exception/       # Custom Exceptions (4)
│   │   │       ├── repository/      # Spring Data Repositories (7)
│   │   │       ├── service/         # Service Interfaces (7)
│   │   │       │   └── impl/        # Service Implementations (7)
│   │   │       └── LibraryManagementSystemApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── css/
│   │           └── application.css  # JavaFX CSS styling
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
- **Database**: MySQL 8.0 (Testcontainers for testing, H2 for dev)
- **ORM**: Spring Data JPA / Hibernate
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Security**: Spring Security (Basic Auth)
- **Desktop GUI**: JavaFX 21 (with JavaFX Maven Plugin)

## Desktop Architecture (Phase 2 - Real Authentication)
- **Entry Point**: `LibraryDesktopApplication.java` extends `Application`
- **Login Screen**: `LoginView.java` with username/password fields, styled login button, loading spinner, error/success messaging
- **Dashboard Screen**: `DashboardView.java` - placeholder with welcome message and logout button
- **Authentication Service**: `AuthService.java` - HTTP Basic auth against Spring Boot backend
- **Styling**: 100% Java-only JavaFX styling via `Theme.java` (no external CSS files)
- **Window**: 1280x800, resizable, centered, titled "My Library"
- **Authentication**: Integrated with Spring Boot backend via HTTP Basic Auth
  - Valid credentials → Dashboard with welcome message
  - Invalid credentials → Clear error message, stays on login
  - Empty fields → Validation message
  - Backend unavailable → Friendly connection error
  - Logout → Returns to login screen, clears auth state
- **Coexistence**: Spring Boot backend and JavaFX client in same Maven project
  - Spring Boot runs on port 8080 (REST API)
  - JavaFX client communicates via HTTP/REST with HTTP Basic Auth
  - Shared DTOs and entities
- **Maven Profile**: `mvn javafx:run` launches desktop app
- **Package Structure**: 
  - `com.library.management.desktop` - Main app class
  - `com.library.management.desktop.ui.login` - Login screen
  - `com.library.management.desktop.ui.dashboard` - Dashboard placeholder
  - `com.library.management.desktop.service` - AuthService
  - `com.library.management.desktop.config` - Future desktop config

## Notes for Future Agents
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
mvn spring-boot:run         # Run Spring Boot backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev  # Run with profile
mvn javafx:run              # Run JavaFX desktop application
```

### Development
```bash
mvn clean install -DskipTests  # Build without tests
```

### Database
```bash
# Application auto-creates schema (ddl-auto=update)
# For fresh schema: mvn spring-boot:run -Dspring.jpa.hibernate.ddl-auto=create
```

### Testing
```bash
mvn test                              # Run unit tests (no Docker needed)
mvn test -Dtest=UserServiceImplTest   # Specific unit test class
mvn test -Pintegration-test           # Run integration tests (Testcontainers, requires Docker)
mvn test -Pintegration-test -Dtest=LibraryManagementSystemApplicationTests # Specific integration test
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

## Runtime Verification Results (2026-09-30)
- **Application startup**: SUCCESS - Spring Boot 3.2.5 started on port 8080 with context path /api
- **Database connection**: SUCCESS - H2 in-memory database (jdbc:h2:mem:library_db) connected, schema auto-created (7 tables)
- **Health endpoint**: SUCCESS - GET /api/actuator/health returns 200 OK with UP status and component details (db, diskSpace, ping)
- **Swagger/OpenAPI**: SUCCESS - GET /api/swagger-ui.html returns 200 OK, GET /api/v3/api-docs returns 200 OK with full OpenAPI 3.0.1 spec
- **API Tests (all with Basic Auth user:password)**:
  - **Users**: GET /api/users (200), POST /api/users (201) - CREATE user works
  - **Authors**: GET /api/authors (200), POST /api/authors (201) - CREATE author works
  - **Categories**: GET /api/categories (200), POST /api/categories (201) - CREATE category works
  - **Books**: GET /api/books (200), POST /api/books (201) - CREATE book works
  - **Borrows**: GET /api/borrows (200), POST /api/borrows (201) - CREATE borrow works
  - **Returns**: GET /api/returns (200), POST /api/returns (201) - CREATE return works
  - **Fines**: GET /api/fines (200), POST /api/fines/generate (200) - Generate fines works
- **Maven test**: BUILD SUCCESS - 8 unit tests passed
- **Files changed**:
  - src/main/java/com/library/management/dto/ApiResponse.java (added error(String,T) and success(String) methods)
  - src/main/java/com/library/management/exception/GlobalExceptionHandler.java (uses new error method)
  - src/main/java/com/library/management/controller/{Category,Book,User,Author,Fine}Controller.java (fixed delete method return types, removed /api prefix from @RequestMapping)
  - src/main/java/com/library/management/service/impl/ReturnServiceImpl.java (added Book import)
  - src/main/java/com/library/management/config/SecurityConfig.java (added UserDetailsService bean for in-memory auth)
  - src/main/resources/application.properties (switched to H2 for dev, added actuator config, fixed credentials)
  - pom.xml (added spring-boot-starter-actuator and h2 dependencies)
- **Remaining issues**: 13 Lombok @Builder initializer warnings (non-blocking)

## Desktop Authentication Verification Results (2026-10-01)
- **Maven version**: Apache Maven 3.9.9
- **Java version**: 21.0.12 (Oracle Corporation)
- **JavaFX version**: 21.0.6
- **Compilation**: BUILD SUCCESS (69 source files compiled)
- **Unit tests**: 8 tests run, 0 failures, 0 errors, 0 skipped (UserServiceImplTest)
- **Desktop Launch**: `mvn javafx:run` - Window opens successfully
- **Authentication Tests** (against Spring Boot backend with dev profile):
  - **Valid credentials (testuser:password123)**: ✅ 200 OK - Dashboard opens with welcome message
  - **Invalid credentials**: ✅ 401 Unauthorized - Clear error message displayed
  - **Empty fields**: ✅ Client-side validation - "Please enter both username and password."
  - **Backend unavailable**: ✅ Connection error handled gracefully - "Cannot connect to server. Please check if the backend is running."
  - **Logout**: ✅ Returns to login screen, clears auth state
- **Files created**:
  - src/main/java/com/library/management/desktop/service/AuthService.java
  - src/main/java/com/library/management/desktop/ui/dashboard/DashboardView.java
- **Files modified**:
  - src/main/java/com/library/management/desktop/LibraryDesktopApplication.java (auth flow)
  - src/main/java/com/library/management/desktop/ui/login/LoginView.java (real auth integration)
  - src/main/resources/css/application.css (dashboard and logout styles)
  - src/main/java/com/library/management/controller/*.java (removed /api prefix)
  - src/main/java/com/library/management/LibraryManagementSystemApplication.java (dev data initializer)
  - src/main/java/com/library/management/entity/User.java (@Builder.Default for membershipDate)
  - src/main/resources/application.properties (dev profile with H2)
- **Remaining issues**: 13 Lombok @Builder initializer warnings (non-blocking)

## Phase 3: "My Library" Desktop Visual Redesign & Verification (2026-10-02)
- **Branding**: "My Library" (replaces all visible "Library Management System" branding)
- **Design System**: JavaFX Java API based Theme (`Theme.java`)
  - Deep plum / near-black palette: `#07050A`, `#0C0916`, `#120E1E`, `#18122B`, `#22183D`
  - Violet/purple accents & ambient glow: `#7C3AED`, `#8B5CF6`, `#A78BFA`, `#C4B5FD`
  - Crisp typography: `#F8FAFC` primary, `#94A3B8` secondary, `#64748B` muted
  - Subdued borders: `#291E3F`, `#3B285E`
- **Key Enhancements**:
  - **Theme Helper**: `Theme.java` provides centralized color palette, custom SVG book logo mark, button stylers (Primary, Secondary, Danger, Pagination), field stylers (TextField, PasswordField, ComboBox), Card & TableView stylers, and recursive Dialog styler.
  - **LoginView**: Deep plum/black radial ambient glow, SVG book icon, "My Library" header with "Your library, beautifully organized." subtitle, dark inputs with focused violet border, password visibility toggle (`👁`), violet primary login button with hover/press scale animation, loading spinner, and real backend authentication intact.
  - **Main Navigation & Window**: Window titled "My Library" (1280x800, min 1000x680), dark plum sidebar with glowing active indicators, clean dark header with current page title, subtitle, user badge pill, and styled logout button.
  - **Dashboard**: Real backend metrics cards with gradients and interactive hover-lift animations (Books, Authors, Categories, Users, Borrows, Pending, Overdue, Fines), live backend status indicator, and loading overlay.
  - **Entity Views & Tables**: Books, Authors, Categories, Users, Borrows, Returns, Fines all styled with dark surfaces, violet hover/selection, pill badges, and pagination controls.
  - **Dialogs**: All modal forms (Create/Edit Book, Author, Category, User, Borrow, Return, Detail) styled with dark surface, white labels, and violet action buttons.
- **Verification**:
  - `mvn clean compile`: BUILD SUCCESS (69 source files).
  - `mvn test -Dtest=DesktopVerificationTest`: 4 tests passed (Theme engine, real auth 200 OK, auth 401 rejection, real API queries).
  - `mvn test -Dtest=UserServiceImplTest`: 8 tests passed.
  - `mvn javafx:run`: Launched and running stably as background daemon (`task-290`).