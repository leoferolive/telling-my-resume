# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Telling My Resume is a Spring Boot 3.2.3 web application that uses AI (Claude and Gemini) to analyze and improve IT resumes. The application allows users to upload resumes in PDF, DOCX, or TXT formats and receive detailed analyses from AI models highlighting strengths and technical competencies relevant to IT positions.

## Core Architecture

### Storage Layer
- **DatabaseStorageService**: Primary storage using H2 database for resume files
- **ResumeRepository**: JPA repository for database operations
- **Resume model**: Entity storing file name, content type, and binary content
- Files stored as byte arrays in H2 database (file-based at `./data/resumedb`)

### AI Services Layer
- **ClaudeService**: Uses Spring AI with Anthropic's Claude 3.5 Sonnet model
  - System prompt loaded from `src/main/resources/prompts/systempromt.st`
  - Configured with temperature 0.7 and max tokens 450
- **GeminiService**: Direct REST API integration with Google's Gemini 1.5 Flash
  - Includes Resilience4j retry logic (3 attempts, 2s wait)
  - Has fallback method for service failures
  - Uses custom VO classes for response mapping

### Web Layer
- **ResumeController**: REST API endpoints with Swagger documentation
- **ResumeViewController**: Web interface controller (Thymeleaf templates)
- **RateLimitingInterceptor**: Request throttling using Bucket4j
- **GlobalExceptionHandler & WebExceptionHandler**: Centralized error handling

### File Processing
- **FileUtils**: Text extraction from PDF (PDFBox), DOCX (Apache POI), and TXT files
- **ResumeFormatter**: Cleans special characters from AI responses

## Development Commands

### Build and Run
```bash
# Run application
./mvnw spring-boot:run

# Build
./mvnw clean compile

# Run tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ResumeServiceTest
```

### Environment Setup
Required environment variables:
- `API_GEMINI_KEY`: Google Gemini API key
- `API_CLAUDE_KEY`: Anthropic Claude API key

### Database Access
H2 console available at: http://localhost:8080/h2-console
- URL: `jdbc:h2:file:./data/resumedb`
- Username: `sa`
- Password: `password`

### API Documentation
Swagger UI available at: http://localhost:8080/swagger-ui.html

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

- Unit tests use JUnit 5 and Mockito
- Test classes mirror main package structure
- Tests include MockMultipartFile for file upload scenarios
- Service layer tests mock dependencies (storage, AI services)
- Tests verify both success and failure scenarios

## API Endpoints

### Core Resume Operations
- `POST /resume/upload`: Upload resume file
- `GET /resume/read/{fileName}`: Read resume content
- `GET /resume/generate/{fileName}`: Generate analysis with Gemini
- `GET /resume/generateClaude/{fileName}`: Generate analysis with Claude
- `GET /resume/view/{fileName}`: View formatted analysis (web interface)

### Error Handling
Custom exceptions for different failure scenarios:
- `ResumeNotFoundException`: File not found
- `ResumeStorageException`: Storage failures
- `InvalidResumeException`: Invalid file format
- `GeminiServiceException`: AI service failures

## Technology Stack Dependencies

- Java 21
- Spring Boot 3.2.3
- Spring AI (Anthropic integration)
- Thymeleaf for web templates
- H2 Database
- Apache PDFBox (PDF processing)
- Apache POI (Office document processing)
- Resilience4j (retry/circuit breaker)
- Bucket4j (rate limiting)
- SpringDoc OpenAPI (API documentation)