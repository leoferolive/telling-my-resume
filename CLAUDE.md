# CLAUDE.md

This file provides comprehensive guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Telling My Resume is a production-ready Spring Boot 3.2.3 web application that leverages AI services (Anthropic Claude and Google Gemini) to analyze and improve IT resumes. The application follows enterprise-grade architecture patterns with emphasis on maintainability, testability, and scalability.

### Key Features
- Multi-format resume processing (PDF, DOCX, TXT)
- Dual AI provider support with failover capabilities
- RESTful API with versioning (v1)
- Web interface for user interactions
- Structured logging with correlation IDs
- Rate limiting and resilience patterns
- Comprehensive error handling with error codes
- API documentation via Swagger/OpenAPI

## Core Architecture

The application follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  Controllers (REST API + Web MVC) + Exception Handlers       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│  Business Logic + AI Integration + Data Processing           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Persistence Layer                         │
│           JPA Repositories + Database Access                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                              │
│                    H2 Database (File-based)                  │
└─────────────────────────────────────────────────────────────┘
```

### Storage Layer
- **DatabaseStorageService**: Primary storage implementation using H2 database
- **ResumeRepository**: JPA repository for CRUD operations on Resume entities
- **Resume model**: JPA entity with fields: id, fileName, contentType, content (BLOB)
- **Storage location**: Files stored as byte arrays in H2 database at `./data/resumedb`
- **Transaction management**: Declarative transactions via Spring @Transactional

### AI Services Layer

#### Service Interfaces
- **AIAnalysisService**: Common interface for AI providers
- **AIProviderService**: Service for managing AI provider status and selection

#### Implementations
- **ClaudeService**: Spring AI integration with Anthropic's Claude 3.5 Sonnet
  - System prompt: `src/main/resources/prompts/systempromt.st`
  - Configuration: temperature 0.7, max tokens 450
  - Uses Spring AI's AnthropicChatModel abstraction

- **GeminiService**: Direct REST API integration with Google Gemini 1.5 Flash
  - Resilience4j retry mechanism (3 attempts, 2s wait between retries)
  - Circuit breaker pattern for service protection
  - Fallback method for graceful degradation
  - Custom VO classes for response mapping (GeminiResponseVO, Candidate, Content, Part)

#### Service Composition
- **ResumeAnalysisService**: Orchestrates resume processing and AI analysis
- **ResumeDataService**: Handles resume data retrieval and validation

### Web Layer

#### Controllers
- **ResumeControllerV1** (`/api/v1/resume`): Versioned REST API with comprehensive endpoints
- **AIProviderControllerV1** (`/api/v1/providers`): AI provider management endpoints
- **ResumeController** (`/resume`): Legacy REST endpoints (maintained for backward compatibility)
- **ResumeViewController** (`/`): Web interface using Thymeleaf templates

#### Cross-cutting Concerns
- **RateLimitingInterceptor**: Token bucket algorithm via Bucket4j (prevents API abuse)
- **CorrelationIdInterceptor**: Request tracking with unique correlation IDs
- **GlobalExceptionHandler**: REST API exception handling
- **WebExceptionHandler**: Web interface exception handling

### DTOs and Validation

#### Request DTOs
- **ResumeUploadRequest**: Multi-part file upload with custom validation
  - `@FileType`: Validates allowed file types (PDF, DOCX, TXT)
  - `@FileSize`: Validates file size limits (max 10MB)

#### Response DTOs
- **ResumeUploadResponse**: Upload confirmation with metadata
- **ResumeAnalysisResponse**: AI analysis results
- **ResumeContentResponse**: Resume content retrieval
- **ErrorResponse**: Standardized error format with error codes and correlation IDs
- **ProviderStatusResponse**: AI provider health status

### File Processing
- **FileUtils**: Multi-format text extraction
  - PDF: Apache PDFBox (version 2.0.27)
  - DOCX: Apache POI (version 5.3.0)
  - TXT: Direct reading with charset detection
- **FileNameSanitizer**: Prevents path traversal attacks
- **ResumeFormatter**: Cleans and formats AI responses (removes special characters)

### Utilities and Constants
- **CorrelationIdUtils**: Manages correlation IDs for request tracking
- **StructuredLogging**: Provides JSON logging format with contextual information
- **ErrorCodes**: Centralized error code definitions
- **ErrorMessages**: Internationalization-ready error messages
- **ApiConstants**: API-related constants (endpoints, headers, etc.)

## Project Structure

```
src/
├── main/
│   ├── java/com/tellingmyresume/
│   │   ├── config/              # Application configuration classes
│   │   │   ├── AIConfig.java    # AI service configurations
│   │   │   ├── AsyncConfig.java # Async processing setup
│   │   │   ├── CorrelationIdInterceptor.java
│   │   │   ├── RestTemplateConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── WebConfig.java   # Web MVC configuration
│   │   ├── constants/           # Application-wide constants
│   │   │   ├── ApiConstants.java
│   │   │   ├── ErrorCodes.java
│   │   │   └── ErrorMessages.java
│   │   ├── controller/          # REST and Web controllers
│   │   │   ├── v1/             # API version 1
│   │   │   │   ├── AIProviderControllerV1.java
│   │   │   │   └── ResumeControllerV1.java
│   │   │   ├── AIProviderController.java (legacy)
│   │   │   ├── ResumeController.java (legacy)
│   │   │   └── ResumeViewController.java
│   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── request/        # Request DTOs
│   │   │   └── response/       # Response DTOs
│   │   ├── exception/          # Custom exceptions and handlers
│   │   │   ├── BaseApplicationException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── [specific exceptions...]
│   │   ├── formatter/          # Data formatters
│   │   ├── interceptor/        # HTTP interceptors
│   │   ├── mapper/             # Object mappers (Entity ↔ DTO)
│   │   ├── model/              # JPA entities
│   │   ├── repository/         # Spring Data repositories
│   │   ├── service/            # Business logic layer
│   │   │   └── impl/           # Service implementations
│   │   ├── util/               # Utility classes
│   │   ├── utils/              # Additional utilities
│   │   └── validation/         # Custom validators
│   └── resources/
│       ├── application.properties
│       ├── prompts/            # AI prompts
│       └── templates/          # Thymeleaf templates
└── test/
    └── java/com/tellingmyresume/
        ├── controller/         # Controller tests
        ├── mapper/            # Mapper tests
        ├── service/           # Service tests
        └── validation/        # Validation tests
```

## Development Commands

### Build and Run
```bash
# Run application (development mode)
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Clean and compile
./mvnw clean compile

# Package application
./mvnw clean package

# Skip tests during package
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=ResumeServiceTest

# Run specific test method
./mvnw test -Dtest=ResumeServiceTest#testUploadResume

# Run integration tests only
./mvnw verify -Dtest=*IntegrationTest

# Check for dependency updates
./mvnw versions:display-dependency-updates
```

### Environment Setup

#### Required Environment Variables
```bash
# API Keys (REQUIRED)
export API_GEMINI_KEY=your_gemini_api_key
export API_CLAUDE_KEY=your_claude_api_key

# Optional - for different environments
export SPRING_PROFILES_ACTIVE=dev  # or prod, test
```

#### Using .env file (recommended for local development)
Create a `.env` file in the project root:
```env
API_GEMINI_KEY=your_gemini_api_key
API_CLAUDE_KEY=your_claude_api_key
```

### Database Access
H2 console available at: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:file:./data/resumedb`
- **Username**: `sa`
- **Password**: `password`
- **Driver Class**: `org.h2.Driver`

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## Key Configuration

### Application Properties
- Server runs on port 8080
- File upload limit: 10MB
- H2 database with file persistence
- JPA shows SQL queries in console
- Resilience4j retry configuration for Gemini service

### AI Model Configuration
- **Claude**: Uses claude-3-5-sonnet-20241022 model
- **Gemini**: Uses gemini-1.5-flash-latest model
- System prompt emphasizes highlighting IT resume strengths without suggesting improvements

## Testing Strategy

### Testing Pyramid
```
    ▲
   /E2E\          End-to-End Tests (few)
  /─────\
 /  INT  \        Integration Tests (some)
/─────────\
/   UNIT   \      Unit Tests (many)
└───────────┘
```

### Unit Testing

#### Best Practices
```java
// ✅ Good - Descriptive test names
@Test
void uploadResume_WithValidPDF_ShouldReturnSuccess() {
    // Arrange
    MultipartFile file = createMockPdfFile();
    when(storageService.store(any())).thenReturn(savedResume);

    // Act
    ResumeUploadResponse response = resumeService.uploadResume(file);

    // Assert
    assertThat(response.getFileName()).isEqualTo("test.pdf");
    verify(storageService, times(1)).store(any());
}

// ✅ Good - Test edge cases
@Test
void uploadResume_WithEmptyFile_ShouldThrowInvalidResumeException() {
    MultipartFile emptyFile = new MockMultipartFile(
        "file", "empty.pdf", "application/pdf", new byte[0]
    );

    assertThatThrownBy(() -> resumeService.uploadResume(emptyFile))
        .isInstanceOf(InvalidResumeException.class)
        .hasMessageContaining("File cannot be empty");
}
```

#### Test Structure Guidelines
- **Arrange-Act-Assert (AAA)** pattern for test organization
- One logical assertion per test (prefer multiple tests over complex assertions)
- Use `@BeforeEach` for common setup
- Clean up resources in `@AfterEach` when needed
- Mock external dependencies (databases, APIs, file systems)

#### Naming Conventions
```
methodName_StateUnderTest_ExpectedBehavior
methodName_WithCondition_ShouldResult
```

Examples:
- `uploadResume_WithValidPDF_ShouldReturnSuccess()`
- `analyzeResume_WhenAIServiceFails_ShouldThrowException()`
- `findByFileName_WithNonExistentFile_ShouldReturnEmpty()`

### Integration Testing

#### Best Practices
```java
@SpringBootTest
@AutoConfigureMockMvc
class ResumeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIAnalysisService aiService;

    @Test
    void uploadResume_IntegrationTest_ShouldPersistToDatabaseAndReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/resume")
                .file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileName").value("test.pdf"));

        // Verify database persistence
        Optional<Resume> saved = resumeRepository.findByFileName("test.pdf");
        assertThat(saved).isPresent();
    }
}
```

#### Integration Test Scope
- Test entire request/response cycle
- Verify database transactions
- Test with real Spring context
- Mock only external APIs (Claude, Gemini)
- Use `@MockBean` for external services

### Test Coverage Guidelines
- **Minimum target**: 80% line coverage
- **Focus areas**:
  - All service layer methods
  - All custom validators
  - All exception handlers
  - Critical business logic
  - Edge cases and error paths

### Testing Utilities
```java
// Test data builders
public class ResumeTestBuilder {
    public static Resume.ResumeBuilder aResume() {
        return Resume.builder()
            .fileName("test.pdf")
            .contentType("application/pdf")
            .content("test content".getBytes());
    }
}

// Usage
Resume resume = aResume()
    .fileName("custom.pdf")
    .build();
```

### Mocking Best Practices
```java
// ✅ Good - Specific mocking
when(aiService.analyze(anyString()))
    .thenReturn("Analysis result");

// ✅ Good - Verify specific interactions
verify(storageService, times(1)).store(argThat(
    resume -> resume.getFileName().equals("test.pdf")
));

// ❌ Bad - Over-mocking
when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
verify(repository, atLeastOnce()).save(any());
```

### Test Organization
```
src/test/java/
├── com/tellingmyresume/
│   ├── controller/          # Controller integration tests
│   │   ├── ResumeControllerIntegrationTest.java
│   │   └── v1/
│   │       └── ResumeControllerV1IntegrationTest.java
│   ├── service/             # Service unit tests
│   │   ├── ResumeServiceTest.java
│   │   └── AIProviderServiceTest.java
│   ├── mapper/              # Mapper tests
│   │   └── ResumeMapperTest.java
│   ├── validation/          # Custom validator tests
│   │   ├── FileTypeValidatorTest.java
│   │   └── FileSizeValidatorTest.java
│   └── util/                # Utility tests
│       └── FileUtilsTest.java
```

## Security Best Practices

### Input Validation and Sanitization

#### File Upload Security
```java
// ✅ Good - Multiple validation layers
@FileType(allowed = {"pdf", "docx", "txt"})  // Whitelist approach
@FileSize(max = 10 * 1024 * 1024)           // Size limit
private MultipartFile file;

// File name sanitization (prevents path traversal)
String sanitizedName = FileNameSanitizer.sanitize(fileName);
```

#### Path Traversal Prevention
```java
// ✅ Good - Sanitize file names
public String sanitize(String fileName) {
    // Remove path separators and dangerous characters
    return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
}

// ❌ Bad - Direct file name usage
File file = new File(uploadDir + "/" + fileName);  // Vulnerable!
```

#### SQL Injection Prevention
- Always use JPA/JPQL with parameterized queries
- Never concatenate user input into queries
```java
// ✅ Good - Parameterized query
@Query("SELECT r FROM Resume r WHERE r.fileName = :fileName")
Optional<Resume> findByFileName(@Param("fileName") String fileName);

// ❌ Bad - String concatenation (vulnerable)
String query = "SELECT * FROM resume WHERE file_name = '" + fileName + "'";
```

### API Security

#### Rate Limiting
```java
// RateLimitingInterceptor configuration
private static final int BUCKET_CAPACITY = 10;      // Max requests
private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

// Prevents DoS attacks and API abuse
```

#### CORS Configuration
```java
// Configure CORS in WebConfig
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins("https://trusted-domain.com")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*")
        .exposedHeaders("X-Correlation-ID")
        .maxAge(3600);
}
```

#### API Key Management
```java
// ✅ Good - Environment variables
@Value("${spring.ai.anthropic.api-key}")
private String claudeApiKey;

// ❌ Bad - Hardcoded keys
private String apiKey = "sk-ant-api03-...";  // NEVER!
```

### Sensitive Data Protection

#### Never Log Sensitive Information
```java
// ✅ Good - Log without sensitive data
log.info("Resume uploaded: fileName={}, size={}",
    sanitizedFileName, file.getSize());

// ❌ Bad - Logging sensitive content
log.info("Resume content: {}", resumeContent);  // May contain PII!
```

#### API Key Security
- Store API keys in environment variables or secure vaults
- Never commit `.env` files or credentials to version control
- Use `.gitignore` to exclude sensitive files
- Rotate API keys regularly

### Error Handling Security

#### Safe Error Messages
```java
// ✅ Good - Generic error for external users
{
  "error": {
    "code": "STORAGE_ERROR",
    "message": "Failed to store resume",
    "correlationId": "abc-123"
  }
}

// ❌ Bad - Exposing internal details
{
  "error": "SQLException: Table 'resumes' not found at /var/db/..."
}
```

### Dependency Security

```bash
# Regularly check for vulnerable dependencies
./mvnw dependency-check:check

# Update dependencies
./mvnw versions:display-dependency-updates

# Check for CVEs
./mvnw org.owasp:dependency-check-maven:check
```

## Performance Best Practices

### Database Optimization

#### Indexing Strategy
```java
@Entity
@Table(name = "resume", indexes = {
    @Index(name = "idx_file_name", columnList = "fileName")
})
public class Resume {
    // Entity definition
}
```

#### Query Optimization
```java
// ✅ Good - Fetch only needed data
@Query("SELECT r.fileName, r.contentType FROM Resume r")
List<Object[]> findFileMetadata();

// ✅ Good - Use pagination for large datasets
Page<Resume> findAll(Pageable pageable);

// ❌ Bad - Fetching everything
List<Resume> findAll();  // Loads all resumes with content!
```

#### Connection Pooling
```properties
# application.properties - HikariCP configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

### Caching Strategies

```java
// ✅ Good - Cache frequently accessed data
@Cacheable(value = "resumes", key = "#fileName")
public Optional<Resume> findByFileName(String fileName) {
    return repository.findByFileName(fileName);
}

// Cache eviction on update/delete
@CacheEvict(value = "resumes", key = "#fileName")
public void deleteResume(String fileName) {
    repository.deleteByFileName(fileName);
}
```

### Asynchronous Processing

```java
// ✅ Good - Async for long-running operations
@Async
@Transactional
public CompletableFuture<String> analyzeResumeAsync(String content) {
    String analysis = aiService.analyze(content);
    return CompletableFuture.completedFuture(analysis);
}

// Usage
CompletableFuture<String> future = service.analyzeResumeAsync(content);
// Continue with other work...
String result = future.get();  // Wait for completion
```

### Resource Management

#### File Handling
```java
// ✅ Good - Try-with-resources for automatic cleanup
try (PDDocument document = PDDocument.load(file.getInputStream());
     PDFTextStripper stripper = new PDFTextStripper()) {
    return stripper.getText(document);
} catch (IOException e) {
    throw new ResumeStorageException("Failed to read PDF", e);
}

// ❌ Bad - Manual resource management
PDDocument document = PDDocument.load(file);
String text = stripper.getText(document);
document.close();  // May not execute if exception occurs!
```

#### Memory Efficiency
```java
// ✅ Good - Stream processing for large files
public Stream<Resume> streamAll() {
    return repository.streamAllBy();
}

// Process in chunks
streamAll().forEach(resume -> {
    // Process each resume
});

// ❌ Bad - Loading everything into memory
List<Resume> allResumes = repository.findAll();
```

### AI Service Optimization

#### Circuit Breaker Pattern
```java
// Resilience4j configuration
@Retry(name = "geminiService", fallbackMethod = "fallbackAnalysis")
@CircuitBreaker(name = "geminiService")
public String analyzeWithGemini(String content) {
    return geminiService.analyze(content);
}

private String fallbackAnalysis(String content, Exception e) {
    log.warn("AI service unavailable, using fallback", e);
    return "AI service temporarily unavailable";
}
```

#### Request Throttling
```java
// Bucket4j rate limiting
private final Bucket bucket = Bucket.builder()
    .addLimit(Limit.of(10, Duration.ofMinutes(1)))
    .build();

if (bucket.tryConsume(1)) {
    // Process request
} else {
    throw new TooManyRequestsException();
}
```

### Monitoring and Profiling

```bash
# Enable JVM metrics
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:+PrintGCDetails \
     -jar application.jar

# Spring Boot Actuator endpoints
/actuator/health
/actuator/metrics
/actuator/prometheus
```

## API Endpoints

### Core Resume Operations (V1)
- `POST /api/v1/resume`: Upload resume file
- `GET /api/v1/resume/{fileName}`: Read resume content
- `POST /api/v1/resume/{fileName}/analyze`: Analyze resume with selected AI provider
- `GET /api/v1/resume`: List all resumes (with pagination)
- `DELETE /api/v1/resume/{fileName}`: Delete resume

### AI Provider Management (V1)
- `GET /api/v1/providers/status`: Get status of all AI providers
- `POST /api/v1/providers/{providerName}/analyze`: Analyze with specific provider

### Legacy Endpoints (Backward Compatibility)
- `POST /resume/upload`: Upload resume file
- `GET /resume/read/{fileName}`: Read resume content
- `GET /resume/generate/{fileName}`: Generate analysis with Gemini
- `GET /resume/generateClaude/{fileName}`: Generate analysis with Claude
- `GET /resume/view/{fileName}`: View formatted analysis (web interface)

### Error Handling
Custom exceptions with error codes for different failure scenarios:

| Exception | Error Code | HTTP Status | Description |
|-----------|-----------|-------------|-------------|
| `ResumeNotFoundException` | `RESUME_NOT_FOUND` | 404 | Resume file not found |
| `InvalidResumeException` | `INVALID_FILE` | 400 | Invalid file format or content |
| `ResumeStorageException` | `STORAGE_ERROR` | 500 | Storage operation failed |
| `ClaudeServiceException` | `AI_SERVICE_ERROR` | 503 | Claude AI service error |
| `GeminiServiceException` | `AI_SERVICE_ERROR` | 503 | Gemini AI service error |
| `ValidationBusinessException` | `VALIDATION_ERROR` | 400 | Request validation failed |

## Technology Stack Dependencies

### Core Framework
- **Java 21**: Modern Java features including records, pattern matching, virtual threads support
- **Spring Boot 3.2.3**: Application framework with auto-configuration
- **Spring Data JPA**: Database access abstraction
- **Spring Validation**: Bean validation support

### AI Integration
- **Spring AI 1.0.0-SNAPSHOT**: Anthropic Claude integration
- **RestTemplate**: Google Gemini API integration

### Web & API
- **Spring Web MVC**: REST API and web interface
- **Thymeleaf**: Server-side template engine
- **SpringDoc OpenAPI 2.6.0**: API documentation (Swagger UI)

### Database
- **H2 Database**: Embedded SQL database with file persistence

### File Processing
- **Apache PDFBox 2.0.27**: PDF text extraction
- **Apache POI 5.3.0**: Microsoft Office document processing

### Resilience & Rate Limiting
- **Resilience4j 2.2.0**: Retry patterns, circuit breaker, fallback mechanisms
- **Bucket4j 7.3.0**: Token bucket rate limiting

### Logging
- **Logstash Logback Encoder 8.0**: Structured JSON logging

### Testing
- **JUnit 5**: Testing framework
- **Mockito 5.8.0**: Mocking framework
- **Spring Boot Test**: Integration testing support

## Coding Standards and Best Practices

### General Principles

#### SOLID Principles
- **Single Responsibility**: Each class has one well-defined purpose
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Subtypes must be substitutable for their base types
- **Interface Segregation**: Prefer specific interfaces over general ones
- **Dependency Inversion**: Depend on abstractions, not concretions

#### Clean Code Practices
- Use meaningful and descriptive names for classes, methods, and variables
- Keep methods short and focused (ideally < 20 lines)
- Avoid deep nesting (max 3 levels)
- Write self-documenting code; comments explain "why", not "what"
- Follow the Boy Scout Rule: leave code cleaner than you found it

### Naming Conventions

#### Java Classes
- **Classes**: PascalCase (e.g., `ResumeService`, `AIProviderController`)
- **Interfaces**: PascalCase with descriptive name (e.g., `StorageService`, `AIAnalysisService`)
- **Abstract Classes**: PascalCase, optionally prefixed with "Abstract" or "Base" (e.g., `BaseApplicationException`)
- **Enums**: PascalCase (e.g., `ErrorCode`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_FILE_SIZE`, `API_VERSION`)

#### Methods and Variables
- **Methods**: camelCase, verb-based (e.g., `uploadResume()`, `analyzeContent()`)
- **Variables**: camelCase, noun-based (e.g., `fileName`, `resumeContent`)
- **Boolean variables**: Prefix with `is`, `has`, `can`, `should` (e.g., `isValid`, `hasContent`)

#### Packages
- All lowercase, singular form
- Follow reverse domain name convention: `com.tellingmyresume.service`

### Package Structure Conventions

```java
// ✅ Good - Clear package organization
com.tellingmyresume.controller.v1      // Versioned controllers
com.tellingmyresume.dto.request        // Request DTOs
com.tellingmyresume.dto.response       // Response DTOs
com.tellingmyresume.service.impl       // Service implementations
com.tellingmyresume.exception          // All exceptions in one place

// ❌ Bad - Mixed concerns
com.tellingmyresume.controller.ResumeControllerV1AndDTO
com.tellingmyresume.stuff
```

### Dependency Injection Best Practices

```java
// ✅ Good - Constructor injection (preferred)
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class ResumeService {
    private final ResumeRepository repository;
    private final StorageService storageService;
}

// ✅ Good - Constructor injection without Lombok
@Service
public class ResumeService {
    private final ResumeRepository repository;

    public ResumeService(ResumeRepository repository) {
        this.repository = repository;
    }
}

// ❌ Avoid - Field injection (harder to test)
@Service
public class ResumeService {
    @Autowired
    private ResumeRepository repository;
}
```

### Exception Handling Patterns

#### Exception Hierarchy
```
BaseApplicationException (abstract)
├── ResumeBusinessException
│   ├── ResumeNotFoundException
│   ├── InvalidResumeException
│   └── ResumeStorageException
├── AIServiceBusinessException
│   ├── ClaudeServiceException
│   ├── GeminiServiceException
│   └── GenericAIServiceException
├── StorageBusinessException
│   └── StorageException
└── ValidationBusinessException
```

#### Best Practices
```java
// ✅ Good - Specific exception with context
throw new ResumeNotFoundException(
    ErrorCodes.RESUME_NOT_FOUND,
    String.format("Resume not found: %s", fileName),
    fileName
);

// ✅ Good - Preserve stack trace
catch (IOException e) {
    throw new ResumeStorageException(
        ErrorCodes.STORAGE_ERROR,
        "Failed to store resume",
        e  // Preserve original exception
    );
}

// ❌ Bad - Generic exception
throw new Exception("Error occurred");

// ❌ Bad - Swallowing exceptions
catch (IOException e) {
    // Silent failure
}
```

### Logging Standards

#### Correlation IDs
Every request automatically gets a correlation ID for tracking across services:
```java
// Automatically added to MDC (Mapped Diagnostic Context)
CorrelationIdUtils.generateCorrelationId();
```

#### Structured Logging
```java
// ✅ Good - Structured logging with context
log.info("Resume uploaded successfully",
    StructuredLogging.of(
        "fileName", fileName,
        "fileSize", fileSize,
        "correlationId", correlationId
    )
);

// ✅ Good - Error logging with exception
log.error("Failed to process resume: {}", fileName, exception);

// ❌ Bad - Unstructured logging
log.info("Resume " + fileName + " uploaded");
```

#### Log Levels
- **ERROR**: Application errors requiring immediate attention
- **WARN**: Potentially harmful situations (e.g., retries, fallbacks)
- **INFO**: High-level informational messages (e.g., service started, resume uploaded)
- **DEBUG**: Detailed diagnostic information (disabled in production)
- **TRACE**: Very detailed diagnostic information (disabled by default)

### Transaction Management

```java
// ✅ Good - Declarative transaction
@Transactional
public Resume saveResume(Resume resume) {
    return resumeRepository.save(resume);
}

// ✅ Good - Read-only transaction for queries
@Transactional(readOnly = true)
public Optional<Resume> findByFileName(String fileName) {
    return resumeRepository.findByFileName(fileName);
}

// ✅ Good - Custom rollback rules
@Transactional(rollbackFor = Exception.class)
public void processResume(Resume resume) {
    // Business logic
}
```

### API Design Best Practices

#### RESTful Conventions
```java
// ✅ Good - RESTful endpoints
POST   /api/v1/resume              // Create resume
GET    /api/v1/resume/{id}         // Get resume by ID
PUT    /api/v1/resume/{id}         // Update resume
DELETE /api/v1/resume/{id}         // Delete resume
GET    /api/v1/resume              // List resumes

// ✅ Good - Action endpoints (non-CRUD)
POST   /api/v1/resume/{id}/analyze // Analyze resume
GET    /api/v1/providers/status    // Get provider status

// ❌ Bad - Non-RESTful
GET    /api/v1/getResume?id=123
POST   /api/v1/doAnalysis
```

#### Versioning
- Use URL versioning: `/api/v1/`, `/api/v2/`
- Maintain backward compatibility for at least one major version
- Deprecate old versions with proper warning headers

#### Response Formats
```java
// ✅ Good - Consistent response structure
{
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00Z",
  "correlationId": "abc-123"
}

// ✅ Good - Error response
{
  "error": {
    "code": "RESUME_NOT_FOUND",
    "message": "Resume not found: example.pdf",
    "correlationId": "abc-123",
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

### Validation Best Practices

```java
// ✅ Good - Custom validation annotations
@FileType(allowed = {"pdf", "docx", "txt"})
@FileSize(max = 10 * 1024 * 1024)  // 10MB
private MultipartFile file;

// ✅ Good - Method-level validation
@Valid
public ResponseEntity<ResumeUploadResponse> uploadResume(
    @Valid @ModelAttribute ResumeUploadRequest request
) {
    // Controller logic
}

// ✅ Good - Manual validation with meaningful errors
if (file.isEmpty()) {
    throw new InvalidResumeException(
        ErrorCodes.INVALID_FILE,
        "File cannot be empty"
    );
}
```
## Git Workflow and Contribution Guidelines

### Branch Naming Convention

```
<type>/<short-description>

Types:
- feature/    : New features
- bugfix/     : Bug fixes
- hotfix/     : Urgent production fixes
- refactor/   : Code refactoring
- docs/       : Documentation only
- test/       : Adding or updating tests
- chore/      : Maintenance tasks
```

Examples:
- `feature/add-pdf-compression`
- `bugfix/fix-gemini-timeout`
- `refactor/improve-error-handling`
- `docs/update-api-documentation`

### Commit Message Convention

Follow the Conventional Commits specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, missing semi-colons, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks

#### Examples
```
feat(resume): add support for RTF file format

Added RTF file parsing capability using Apache Tika.
Updated file validators to accept .rtf extensions.

Closes #123
```

```
fix(ai): prevent timeout on large resume analysis

Increased timeout configuration for AI services from 30s to 60s.
Added connection pool configuration for RestTemplate.

Fixes #456
```

```
refactor(service): extract AI provider factory pattern

Moved AI provider instantiation logic to factory class.
Improves testability and follows dependency injection patterns.
```

### Development Workflow

```bash
# 1. Create and checkout feature branch
git checkout -b feature/add-new-feature

# 2. Make changes and commit frequently
git add .
git commit -m "feat(scope): description"

# 3. Keep branch updated with main
git fetch origin
git rebase origin/main

# 4. Push to remote
git push -u origin feature/add-new-feature

# 5. Create pull request via GitHub UI
```

### Code Review Checklist

#### Before Creating PR
- [ ] All tests pass locally (`./mvnw test`)
- [ ] Code compiles without warnings (`./mvnw clean compile`)
- [ ] New tests added for new functionality
- [ ] Documentation updated (if needed)
- [ ] No commented-out code
- [ ] No console.log or debug statements
- [ ] Git history is clean (squash if needed)
- [ ] Branch is up to date with main

#### PR Description Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project conventions
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
```

### Code Review Guidelines

#### Reviewer Responsibilities
- Review within 24 hours
- Focus on:
  - Logic and correctness
  - Security vulnerabilities
  - Performance implications
  - Code readability
  - Test coverage
- Be constructive and respectful
- Approve only when all concerns addressed

#### Author Responsibilities
- Respond to all feedback
- Make requested changes promptly
- Ask questions if feedback unclear
- Update PR description if scope changes

## Common Development Tasks

### Adding a New AI Provider

1. **Create Service Interface Implementation**
```java
@Service
public class NewAIService implements AIAnalysisService {
    @Override
    public String analyze(String content) {
        // Implementation
    }
}
```

2. **Add Configuration**
```properties
# application.properties
spring.ai.new-provider.api-key=${API_NEW_PROVIDER_KEY}
spring.ai.new-provider.model=model-name
```

3. **Create Custom Exception**
```java
public class NewAIServiceException extends AIServiceBusinessException {
    public NewAIServiceException(String message) {
        super(ErrorCodes.AI_SERVICE_ERROR, message);
    }
}
```

4. **Add Tests**
```java
@Test
void analyze_WithValidContent_ShouldReturnAnalysis() {
    // Test implementation
}
```

5. **Update Documentation**
- Add to CLAUDE.md
- Update Swagger documentation
- Update README.md

### Adding a New File Format

1. **Update FileUtils**
```java
public static String extractText(MultipartFile file) {
    String contentType = file.getContentType();
    return switch (contentType) {
        case "application/pdf" -> extractFromPDF(file);
        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            -> extractFromDOCX(file);
        case "application/new-format" -> extractFromNewFormat(file);
        default -> throw new InvalidResumeException("Unsupported format");
    };
}
```

2. **Update Validation**
```java
@FileType(allowed = {"pdf", "docx", "txt", "new-format"})
```

3. **Add Tests**
```java
@Test
void extractText_FromNewFormat_ShouldReturnText() {
    // Test implementation
}
```

### Debugging Tips

#### Enable Debug Logging
```properties
# application.properties
logging.level.com.tellingmyresume=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

#### Common Issues and Solutions

**Issue: AI Service Timeout**
```java
// Solution: Increase timeout in RestTemplateConfig
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(60000);  // Increase to 60s
    factory.setReadTimeout(60000);
    return new RestTemplate(factory);
}
```

**Issue: H2 Database Lock**
```bash
# Solution: Stop all running instances and delete lock file
rm ./data/resumedb.lock.db
```

**Issue: Out of Memory During Large File Processing**
```bash
# Solution: Increase JVM heap size
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2g"
```

## Documentation Standards

### Javadoc Requirements

```java
/**
 * Analyzes resume content using AI services.
 *
 * <p>This method sends the resume content to the configured AI provider
 * and returns a structured analysis highlighting key strengths and
 * technical competencies.
 *
 * @param content the resume text content to analyze
 * @return the AI-generated analysis
 * @throws AIServiceException if the AI service is unavailable
 * @throws IllegalArgumentException if content is null or empty
 * @see AIAnalysisService
 * @since 1.0.0
 */
public String analyzeResume(String content) {
    // Implementation
}
```

### When to Add Comments

```java
// ✅ Good - Explain complex business logic
// Calculate retry delay using exponential backoff with jitter
// to prevent thundering herd problem
long delay = (long) (Math.pow(2, attempt) * 1000 + random.nextInt(1000));

// ✅ Good - Explain non-obvious decisions
// Using LinkedHashMap to maintain insertion order for consistent
// JSON serialization in API responses
Map<String, Object> response = new LinkedHashMap<>();

// ❌ Bad - Stating the obvious
// Increment counter by 1
counter++;

// ❌ Bad - Commented-out code (use git history instead)
// return oldImplementation(data);
```

### README vs CLAUDE.md

**README.md**: User-facing documentation
- Quick start guide
- Installation instructions
- Basic usage examples
- License and contribution info

**CLAUDE.md**: Developer-facing documentation
- Architecture details
- Coding standards
- Development workflows
- Best practices

## Troubleshooting Guide

### Application Won't Start

1. **Check API keys are set**
```bash
echo $API_CLAUDE_KEY
echo $API_GEMINI_KEY
```

2. **Check Java version**
```bash
java -version  # Should be Java 21
```

3. **Check port availability**
```bash
lsof -i :8080  # Check if port 8080 is in use
```

### Tests Failing

1. **Clean and rebuild**
```bash
./mvnw clean install
```

2. **Check test database**
```bash
# Delete test database
rm ./data/resumedb-test.*
```

3. **Run specific failing test with debug**
```bash
./mvnw test -Dtest=ResumeServiceTest#failingTestMethod -X
```

### API Not Responding

1. **Check application logs**
```bash
tail -f logs/application.log
```

2. **Verify service health**
```bash
curl http://localhost:8080/actuator/health
```

3. **Check rate limiting**
```bash
# Rate limit may be triggered; wait 1 minute or restart application
```

## Additional Resources

### Internal Documentation
- API Documentation: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
- Actuator Endpoints: http://localhost:8080/actuator

### External References
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Anthropic Claude API](https://docs.anthropic.com/)
- [Google Gemini API](https://ai.google.dev/docs)
- [Resilience4j Documentation](https://resilience4j.readme.io/)

### Useful Commands Reference

```bash
# Development
./mvnw spring-boot:run                          # Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Testing
./mvnw test                                     # Run all tests
./mvnw test -Dtest=ClassName                    # Run specific test class
./mvnw verify                                   # Run tests + integration tests
./mvnw test jacoco:report                       # Generate coverage report

# Build
./mvnw clean compile                            # Compile only
./mvnw clean package                            # Build JAR
./mvnw clean package -DskipTests               # Build without tests

# Dependencies
./mvnw dependency:tree                          # Show dependency tree
./mvnw versions:display-dependency-updates      # Check for updates
./mvnw dependency-check:check                   # Security scan

# Code Quality
./mvnw spotless:check                          # Check code formatting
./mvnw spotless:apply                          # Apply code formatting

# Database
rm -rf ./data/resumedb*                        # Reset database

# Logs
tail -f logs/application.log                   # Follow logs
grep ERROR logs/application.log                # Find errors
```

## Version History

### Version 0.0.3 (Current)
- Added correlation ID support for request tracking
- Implemented v1 API with enhanced error handling
- Added structured JSON logging
- Improved exception hierarchy with error codes
- Added AI provider management endpoints

### Version 0.0.2
- Added Gemini AI integration
- Implemented rate limiting
- Added Resilience4j retry patterns
- Enhanced file validation

### Version 0.0.1
- Initial release
- Claude AI integration
- Basic resume upload and analysis
- H2 database storage
- Web interface with Thymeleaf

---

**Last Updated**: 2025-01-15
**Maintained By**: Development Team
**Questions?**: Create an issue in the project repository
