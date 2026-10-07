# My Library

A modern library desktop application and REST API backend built with **Java 21**, **Spring Boot 3.2**, **Maven**, **MySQL**, and **JavaFX** for the desktop client.

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.5
- **Build Tool**: Maven
- **Database**: MySQL 8.0 (H2 for development)
- **ORM**: Spring Data JPA / Hibernate
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Security**: Spring Security with Basic Auth (extensible to JWT)
- **Validation**: Bean Validation (Jakarta Validation)
- **Desktop GUI**: JavaFX 21

## Project Structure

```
librarymanagementsystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/library/management/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST Controllers
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── entity/          # JPA Entities
│   │   │       ├── exception/       # Custom Exceptions
│   │   │       ├── repository/      # Spring Data Repositories
│   │   │       ├── service/         # Service Interfaces
│   │   │       │   └── impl/        # Service Implementations
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

## Database Schema

The system includes the following core entities:

- **User/Member** - Library members with roles (ADMIN, LIBRARIAN, MEMBER)
- **Book** - Books with ISBN, title, author, category, availability
- **Author** - Book authors
- **Category** - Book categories/genres
- **Borrow/Loan** - Book borrowing records
- **Return** - Book return records
- **Fine** - Overdue/lost book fines

## Setup Instructions

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+ (or use Testcontainers for testing)

### Database Setup

1. Install MySQL 8.0+
2. Create a database (or let Hibernate auto-create):
   ```sql
   CREATE DATABASE library_db;
   ```
3. Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Build and Run

### Spring Boot Backend

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package

# Run the Spring Boot backend
mvn spring-boot:run
```

The backend will start on `http://localhost:9090/api`

### Demo Account (Academic Presentation)

The application includes a pre-configured demonstration account stored with BCrypt password hashing in MySQL:

- **Username**: `demo`
- **Password**: `demo123`
- **Role**: `ADMIN`

### JavaFX Desktop Application

```bash
# Run the JavaFX desktop application
mvn javafx:run
```

This launches the native Windows desktop application with the login screen.

### Run Both (Full Demonstration)

For demonstration, run both in separate terminals:

```bash
# Terminal 1: Spring Boot Backend
mvn spring-boot:run

# Terminal 2: JavaFX Desktop Client
mvn javafx:run
```

### API Documentation

- Swagger UI: `http://localhost:9090/api/swagger-ui.html`
- OpenAPI Spec: `http://localhost:9090/api/v3/api-docs`

### Health Check

- Actuator Health: `http://localhost:8080/api/actuator/health`

## API Endpoints

### Users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users` - List users (paginated)
- `GET /api/users/search?q={query}` - Search users
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/status/{status}` - Get users by status
- `PUT /api/users/{id}` - Update user
- `PATCH /api/users/{id}/status` - Update user status
- `PATCH /api/users/{id}/role` - Update user role
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/expired` - Get expired users

### Books
- `POST /api/books` - Create book
- `GET /api/books/{id}` - Get book by ID
- `GET /api/books/isbn/{isbn}` - Get book by ISBN
- `GET /api/books` - List books (paginated)
- `GET /api/books/search?q={query}` - Search books
- `GET /api/books/author/{authorId}` - Get books by author
- `GET /api/books/category/{categoryId}` - Get books by category
- `GET /api/books/status/{status}` - Get books by status
- `PUT /api/books/{id}` - Update book
- `PATCH /api/books/{id}/status` - Update book status
- `DELETE /api/books/{id}` - Delete book
- `GET /api/books/count/status/{status}` - Count books by status

### Authors
- `POST /api/authors` - Create author
- `GET /api/authors/{id}` - Get author by ID
- `GET /api/authors` - List authors (paginated)
- `GET /api/authors/search?q={query}` - Search authors
- `PUT /api/authors/{id}` - Update author
- `DELETE /api/authors/{id}` - Delete author

### Categories
- `POST /api/categories` - Create category
- `GET /api/categories/{id}` - Get category by ID
- `GET /api/categories` - List categories (paginated)
- `GET /api/categories/search?q={query}` - Search categories
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Borrows
- `POST /api/borrows` - Borrow a book
- `GET /api/borrows/{id}` - Get borrow by ID
- `GET /api/borrows` - List borrows (paginated)
- `GET /api/borrows/user/{userId}` - Get borrows by user (paginated)
- `GET /api/borrows/user/{userId}/status/{status}` - Get borrows by user and status
- `GET /api/borrows/book/{bookId}` - Get borrows by book
- `GET /api/borrows/overdue` - Get overdue borrows
- `POST /api/borrows/{id}/return` - Return a book
- `POST /api/borrows/{id}/lost` - Mark book as lost
- `GET /api/borrows/user/{userId}/active-count` - Get active borrows count
- `GET /api/borrows/user/{userId}/book/{bookId}/active` - Check active borrow

### Returns
- `POST /api/returns` - Create return record
- `GET /api/returns/{id}` - Get return by ID
- `GET /api/returns` - List returns (paginated)
- `GET /api/returns/user/{userId}` - Get returns by user

### Fines
- `GET /api/fines/{id}` - Get fine by ID
- `GET /api/fines` - List fines (paginated)
- `GET /api/fines/user/{userId}` - Get fines by user (paginated)
- `GET /api/fines/user/{userId}/status/{status}` - Get fines by user and status
- `GET /api/fines/user/{userId}/total-unpaid` - Get total unpaid fines
- `POST /api/fines/{id}/pay` - Pay fine
- `POST /api/fines/{id}/waive` - Waive fine
- `POST /api/fines/generate` - Generate fines for overdue books

## Authentication

Currently uses **HTTP Basic Auth** for simplicity. For production, implement JWT-based authentication.

Default users (after running with `spring.jpa.hibernate.ddl-auto=create`):
- Admin: Create via API with role ADMIN

## Configuration

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8080
server.servlet.context-path=/api
```

## Testing

### Unit Tests
Run standard unit tests (no Docker required):
```bash
# Run all unit tests
mvn test

# Run a specific unit test class
mvn test -Dtest=UserServiceImplTest
```

### Integration Tests (Docker Required)
Integration tests (such as `LibraryManagementSystemApplicationTests`) use **Testcontainers** to launch a real `mysql:8.0` container:
```bash
# Run integration tests requiring Docker
mvn test -Pintegration-test

# Run a specific integration test
mvn test -Pintegration-test -Dtest=LibraryManagementSystemApplicationTests
```

> **Note on Docker Requirement**:
> If Docker is not installed or the Docker daemon is not running, executing integration tests will fail with:
> `java.lang.IllegalStateException: Could not find a valid Docker environment`
> Ordinary unit tests (`mvn test`) and application packaging (`mvn package`) do not require Docker and will build and test cleanly without it.

## Desktop Application Architecture

The desktop application follows a clean separation between the JavaFX client and Spring Boot backend:

```
com.library.management.desktop
├── LibraryDesktopApplication.java    # JavaFX entry point (1280x800 centered)
├── theme/
│   └── Theme.java                    # Scholarly reading room design system (Java-only)
├── service/
│   ├── ApiClient.java               # Non-blocking HTTP client (CompletableFuture)
│   ├── ApiService.java              # Entity service operations
│   └── AuthService.java             # HTTP Basic authentication
├── dto/                             # Desktop transfer objects
└── ui/
    ├── login/                       # Atmospheric Sign In ("My Library")
    ├── main/                        # Shell layout, header & gold-accented sidebar
    ├── dashboard/                   # Real-time metrics cards & quick action toolbar
    ├── books/                       # Books catalog, search, status badges & table
    ├── authors/                     # Authors catalog & biography management
    ├── categories/                  # Categories taxonomy & genres
    ├── users/                       # Patrons & library staff management
    ├── borrows/                     # Active circulation & loan desk
    ├── returns/                     # Return check-in & condition assessment
    ├── fines/                       # Fine tracking, payments & waivers
    └── common/                      # BookishBackground, dialogs, overlays & empty states
```

### Key Points
- Branded as **My Library**
- Aesthetic: **A modern digital library inside a scholarly reading room**
- Palette: Deep plum (`#0A0710`), rich burgundy (`#260E1E`), warm leather, parchment (`#F4EBD9`), muted gold (`#C5A059`), subtle violet (`#7C3AED`)
- Atmospheric `BookishBackground`: Faint Latin manuscript text watermarks, ambient burgundy glow, book spine silhouettes, and delicate fleurons
- Non-blocking network requests using `CompletableFuture` dispatched safely to JavaFX thread via `Platform.runLater`
- Spring Boot backend runs independently (REST API on port 9090)
- JavaFX client communicates with backend via HTTP/REST with HTTP Basic Auth
- 100% Java-only styling via `Theme.java` (no brittle external CSS files)
- Real MySQL database-backed operations and live metrics (zero fake or mock data)
- All 10 create/edit dialogs audited with validated workflows and immediate parent table updates

## Development

### Code Style

The project uses standard Java conventions. Run formatting checks:

```bash
# Verify formatting (if using spotless or similar)
mvn spotless:check

# Apply formatting
mvn spotless:apply
```

### Building for Production

```bash
mvn clean package -DskipTests
```

This creates a fat JAR in `target/` that can be run with:

```bash
java -jar target/library-management-system-1.0.0.jar
```

## Future Enhancements

- JWT-based authentication and authorization
- Email notifications for due dates
- Book reservation system
- Reporting and analytics dashboard
- Integration with external library systems
- Mobile API optimizations
- Audit logging
- Multi-language support

## License

MIT License
