# API Validation Response Utils

A comprehensive Spring Boot library for standardized API responses, validation utilities, and exception handling.

## Overview

This library provides a complete solution for building RESTful APIs with:
- **Standardized API Responses** - Consistent response format across your application
- **Validation Utilities** - Comprehensive field validation helpers
- **Custom Exceptions** - Pre-defined exception types for common scenarios
- **Exception Handlers** - Global exception handling with proper HTTP status codes
- **Custom Validators** - Jakarta Bean Validation annotations
- **Utility Classes** - BigDecimal operations and more

## Installation

Add JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.iamprovy-dev</groupId>
        <artifactId>standard-api-response-business-utils</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

> **Note**: Check JitPack for the latest version. Visit: https://jitpack.io/#iamprovy-dev/standard-api-response-business-utils

## Table of Contents

1. [API Response](#api-response)
2. [Response Entity Builder](#response-entity-builder)
3. [Exception Classes](#exception-classes)
4. [Global Exception Handler](#global-exception-handler)
5. [Validation Utilities](#validation-utilities)
6. [Custom Validators](#custom-validators)
7. [Collection Models](#collection-models)
8. [Enums](#enums)
9. [BigDecimal Utils](#bigdecimal-utils)

---

## API Response

The `ApiResponse<T>` class provides a standardized response structure for all API endpoints.

### Class Structure

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {
    private int code;
    private String message;
    private boolean success;
    private T data;
}
```

### Example Response Format

```json
{
  "code": 200,
  "message": "Operation successful",
  "success": true,
  "data": { ... }
}
```

---

## Response Entity Builder

The `ResponseEntityBuilder` provides convenient methods to create Spring `ResponseEntity` objects with standardized responses.

### Usage Examples

```java
import zw.saas.validation.responses.ResponseEntityBuilder;
import zw.saas.validation.responses.ApiResponse;

// 1. Return OK response (200)
@GetMapping("/users/{id}")
public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseEntityBuilder.ok("User retrieved successfully", user);
}

// 2. Return created response (201)
@PostMapping("/users")
public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
    User created = userService.save(user);
    return ResponseEntityBuilder.created("User created successfully", created);
}

// 3. Return bad request (400)
@GetMapping("/validate")
public ResponseEntity<ApiResponse<Void>> validate(@RequestParam String email) {
    if (!isValid(email)) {
        return ResponseEntityBuilder.badRequest("Invalid email format", null);
    }
    return ResponseEntityBuilder.ok("Validation passed", null);
}

// 4. Return not found (404)
@GetMapping("/users/{id}")
public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(user -> ResponseEntityBuilder.ok("User found", user))
        .orElse(ResponseEntityBuilder.notFound("User not found", null));
}

// 5. Return conflict/duplicate (409)
@PostMapping("/users")
public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
    if (userService.existsByEmail(user.getEmail())) {
        return ResponseEntityBuilder.alreadyExisting("User already exists", null);
    }
    return ResponseEntityBuilder.created("User created", userService.save(user));
}

// 6. Return internal server error (500)
@GetMapping("/data")
public ResponseEntity<ApiResponse<Data>> getData() {
    try {
        return ResponseEntityBuilder.ok("Data retrieved", dataService.getData());
    } catch (Exception e) {
        return ResponseEntityBuilder.internalServerError("An error occurred", null);
    }
}

// 7. Custom status response
@GetMapping("/custom")
public ResponseEntity<ApiResponse<Void>> customResponse() {
    return ResponseEntityBuilder.build(
        HttpStatus.ACCEPTED, 
        "Custom message", 
        customData
    );
}
```

### Available Methods

| Method | HTTP Status | Description |
|--------|-------------|-------------|
| `ok(message, data)` | 200 OK | Successful operation |
| `created(message, data)` | 201 Created | Resource created |
| `badRequest(message, data)` | 400 Bad Request | Validation error |
| `notFound(message, data)` | 404 Not Found | Resource not found |
| `alreadyExisting(message, data)` | 409 Conflict | Duplicate resource |
| `internalServerError(message, data)` | 500 Internal Server Error | Server error |
| `build(status, message, data)` | Custom | Custom HTTP status |

---

## API Response Factory

The `ApiResponseFactory` creates `ApiResponse` objects without the HTTP wrapper.

```java
import zw.saas.validation.responses.ApiResponseFactory;
import zw.saas.validation.responses.ApiResponse;

// Create success response
ApiResponse<User> response = ApiResponseFactory.success("User found", user);

// Create created response
ApiResponse<User> response = ApiResponseFactory.created("User created", user);

// Create failed response
ApiResponse<Void> response = ApiResponseFactory.failed("Validation failed", null);

// Create not found response
ApiResponse<Void> response = ApiResponseFactory.notFound("User not found", null);

// Create duplicate response
ApiResponse<Void> response = ApiResponseFactory.duplicate("User already exists", null);

// Create custom response
ApiResponse<Void> response = ApiResponseFactory.of(
    HttpStatus.ACCEPTED, 
    "Custom message", 
    null
);
```

---

## Exception Classes

The library provides a comprehensive set of custom exception classes.

### Available Exceptions

| Exception | HTTP Status | Use Case |
|-----------|-------------|----------|
| `ValidationException` | 400 | Field validation errors |
| `AlreadyExistsException` | 409 | Resource already exists |
| `NotFoundException` | 404 | Resource not found |
| `ResourceNotFoundException` | 404 | Specific resource not found |
| `DoesNotExistException` | 404 | Resource doesn't exist |
| `MissingFieldsException` | 400 | Required fields missing |
| `UnauthorizedException` | 401 | Authentication required |
| `NotAllowedException` | 403 | Access forbidden |
| `InvalidException` | 400 | Invalid input |
| `InsufficientFundsException` | 400 | Payment insufficient funds |
| `RequestFailedException` | 500 | Request processing failed |

### Usage Examples

```java
import zw.saas.validation.responses.exception.*;

// Throwing validation exception
public void validateUser(User user) {
    if (user.getEmail() == null || user.getEmail().isBlank()) {
        throw new ValidationException("Email is required");
    }
    if (!user.getEmail().contains("@")) {
        throw new InvalidException("Invalid email format");
    }
}

// Throwing not found exception
public User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
}

// Throwing already exists exception
public void createUser(User user) {
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new AlreadyExistsException("User with email " + user.getEmail() + " already exists");
    }
    userRepository.save(user);
}

// Throwing unauthorized exception
public void checkAuthentication(Principal principal) {
    if (principal == null) {
        throw new UnauthorizedException("Authentication required");
    }
}

// Throwing missing fields exception
public void validateRequiredFields(UserDto dto) {
    List<String> missing = new ArrayList<>();
    if (dto.getName() == null) missing.add("name");
    if (dto.getEmail() == null) missing.add("email");
    
    if (!missing.isEmpty()) {
        throw new MissingFieldsException("Missing required fields: " + String.join(", ", missing));
    }
}
```

---

## Global Exception Handler

The `GlobalExceptionHandler` automatically handles exceptions and returns proper HTTP responses.

### Setup

```java
import zw.saas.validation.responses.exception.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler extends GlobalExceptionHandler {
    // Inherits all exception handlers from the parent class
}
```

### Handled Exceptions

The handler automatically converts these exceptions to proper API responses:

```java
// MethodArgumentNotValidException -> 400 Bad Request
// ValidationException -> 400 Bad Request
// AlreadyExistsException -> 409 Conflict
// DoesNotExistException -> 404 Not Found
// NotFoundException -> 404 Not Found
// ResourceNotFoundException -> 404 Not Found
// UnauthorizedException -> 401 Unauthorized
// MissingFieldsException -> 400 Bad Request
// RequestFailedException -> 500 Internal Server Error
// Generic Exception -> 500 Internal Server Error
```

### Example Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(
            @Valid @RequestBody UserDto userDto) {
        // If validation fails, GlobalExceptionHandler handles it automatically
        User user = userService.create(userDto);
        return ResponseEntityBuilder.created("User created successfully", user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        // Throws NotFoundException if not found
        User user = userService.findByIdOrThrow(id);
        return ResponseEntityBuilder.ok("User retrieved", user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntityBuilder.ok("User deleted", null);
    }
}
```

---

## Validation Utilities

The `GlobalFieldValidator` provides static methods for common validation scenarios.

### String Validation

```java
import zw.saas.validation.responses.validation.GlobalFieldValidator;

// Validate string length
GlobalFieldValidator.validateString(name, "Name", 2, 50);

// Validate email format
GlobalFieldValidator.validateEmail(email, "Email");

// Validate phone number
GlobalFieldValidator.validatePhone(phone, "Phone number");

// Validate URL
GlobalFieldValidator.validateUrl(url, "Website URL");
```

### Date Validation

```java
import java.time.LocalDate;

// Validate date is not null
GlobalFieldValidator.validateLocalDate(date, "Date");

// Validate payment date is not in the future
GlobalFieldValidator.validatePaymentDate(paymentDate, "Payment date");

// Validate date of birth (must be at least 2 years old)
GlobalFieldValidator.validateDateOfBirth(dob, "Date of birth");

// Validate opening date (cannot be more than 7 days in the past)
GlobalFieldValidator.validateOpeningDate(openingDate, "Opening date");

// Validate one date is before another
GlobalFieldValidator.validateDatesBeforeAnotherDates(startDate, endDate, "End date");
```

### Numeric Validation

```java
import java.math.BigDecimal;

// Validate positive long
GlobalFieldValidator.validatePositiveLong(id, "User ID");

// Validate positive amount
GlobalFieldValidator.validatePositiveAmount(amount, "Amount");

// Validate amount with minimum
GlobalFieldValidator.validateAmount(amount, "Fee", new BigDecimal("10.00"));

// Validate percentage (0-100)
GlobalFieldValidator.validatePercentage(discount, "Discount");

// Validate integer
GlobalFieldValidator.validateInteger(quantity, "Quantity");

// Validate period (days)
GlobalFieldValidator.validatePeriod(period, "Period");

// Validate size limit
GlobalFieldValidator.validateSizeLimit(size, "Size");

// Validate fee
GlobalFieldValidator.validateFee(fee, "Fee");
```

### Collection Validation

```java
import java.util.List;

// Validate non-null
GlobalFieldValidator.validateNonNull(value, "Value");

// Validate non-empty list
GlobalFieldValidator.validateNonEmptyList(items, "Items");

// Validate boolean
GlobalFieldValidator.validateBoolean(flag, "Flag");
```

### UUID Validation

```java
// Validate UUID string
GlobalFieldValidator.validateUUID(uuidString, "UUID");
```

### Enum Validation

```java
import zw.saas.validation.responses.enums.Gender;

// Validate enum value
GlobalFieldValidator.validateEnum(Gender.class, gender, "Gender");
```

### Complex Object Validation

```java
import zw.saas.validation.responses.collections.Contacts;
import zw.saas.validation.responses.collections.Guardian;

// Validate contact object
Contacts contact = new Contacts();
contact.setContactPerson("John Doe");
contact.setContactNumber("+263771234567");
contact.setEmailAddress("john@example.com");
GlobalFieldValidator.validateContact(contact, 0); // index 0 for first contact

// Validate guardian object
Guardian guardian = new Guardian();
guardian.setName("Jane");
guardian.setSurname("Doe");
guardian.setMobile("+263771234567");
guardian.setAddress("123 Main St");
guardian.setRelationship(Relationship.FATHER);
GlobalFieldValidator.validateGuardian(guardian);
```

---

## Custom Validators

The library provides Jakarta Bean Validation annotations for use in DTOs and entities.

### @ValidUuid

Validates that a UUID field contains a valid UUID format.

```java
import zw.saas.validation.responses.validation.ValidUuid;
import java.util.UUID;

public class UserDto {
    @ValidUuid
    private UUID userId;
    
    private String name;
}
```

### @ValidLongRange

Validates that a Long value is within a specified range.

```java
import zw.saas.validation.responses.validation.ValidLongRange;

public class QueryDto {
    @ValidLongRange(min = 1, max = 100)
    private Long pageSize;
    
    @ValidLongRange(min = 0)
    private Long pageNumber;
}
```

### @ValidEnum

Validates that a string value is a valid enum constant.

```java
import zw.saas.validation.responses.validation.ValidEnum;
import zw.saas.validation.responses.enums.Gender;

public class UserDto {
    @ValidEnum(enumClass = Gender.class)
    private String gender;
}
```

### Using with Jakarta Validation

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CreateUserRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Must be at least 18 years old")
    private Integer age;

    @Valid
    private AddressDto address;
}
```

---

## Collection Models

### Contacts

Embeddable class for storing contact information.

```java
import zw.saas.validation.responses.collections.Contacts;

Contacts contact = new Contacts();
contact.setContactPerson("John Doe");
contact.setContactNumber("+263771234567");
contact.setDesignation("Manager");
contact.setEmailAddress("john@example.com");
```

### Guardian

Embeddable class for storing guardian/parent information.

```java
import zw.saas.validation.responses.collections.Guardian;
import zw.saas.validation.responses.enums.Relationship;

Guardian guardian = new Guardian();
guardian.setName("Jane");
guardian.setSurname("Doe");
guardian.setMobile("+263771234567");
guardian.setAddress("123 Main Street, Harare");
guardian.setRelationship(Relationship.FATHER);
```

### StaffQualifications

Embeddable class for storing staff qualifications.

```java
import zw.saas.validation.responses.collections.StaffQualifications;
import zw.saas.validation.responses.enums.Qualifications;
import java.time.LocalDate;

StaffQualifications qualification = new StaffQualifications();
qualification.setQualifications(Qualifications.MASTERS);
qualification.setQualificationName("Master of Science in Computer Science");
qualification.setYearObtained(LocalDate.of(2020, 5, 15));
qualification.setSubjectSpecialised("Software Engineering");
qualification.setInstitution("University of Zimbabwe");
```

---

## Enums

### Gender

```java
import zw.saas.validation.responses.enums.Gender;

Gender gender = Gender.MALE;
System.out.println(gender.getDescription()); // "Male gender, typically associated with masculinity."
```

### Qualifications

```java
import zw.saas.validation.responses.enums.Qualifications;

Qualifications qual = Qualifications.DEGREE;
System.out.println(qual.getDescription()); // "Bachelor's Degree"
```

### Relationship

```java
import zw.saas.validation.responses.enums.Relationship;

Relationship relationship = Relationship.MOTHER;
```

### Religion

```java
import zw.saas.validation.responses.enums.Religion;

Religion religion = Religion.CHRISTIANITY;
```

---

## BigDecimal Utils

The `BigDecimalUtils` class provides null-safe operations for BigDecimal values.

### Null Safety

```java
import zw.saas.validation.responses.configs.BigDecimalUtils;
import java.math.BigDecimal;

// Return zero if null
BigDecimal amount = BigDecimalUtils.valueOrZero(null); // returns BigDecimal.ZERO

// Return default value if null
BigDecimal amount = BigDecimalUtils.valueOrDefault(null, BigDecimal.TEN); // returns BigDecimal.TEN

// Check if null or zero
boolean isZero = BigDecimalUtils.isNullOrZero(amount);

// Check if null or negative
boolean isNegative = BigDecimalUtils.isNullOrNegative(amount);
```

### Arithmetic Operations

```java
// Add two values (null-safe)
BigDecimal sum = BigDecimalUtils.add(value1, value2);

// Subtract (null-safe)
BigDecimal difference = BigDecimalUtils.subtract(value1, value2);

// Multiply (null-safe)
BigDecimal product = BigDecimalUtils.multiply(value1, value2);

// Divide with specified scale and rounding
BigDecimal quotient = BigDecimalUtils.divide(value1, value2, 2, RoundingMode.HALF_UP);
```

### Formatting

```java
// Format to 2 decimal places
String formatted = BigDecimalUtils.format(amount);

// Format with custom scale
String formatted = BigDecimalUtils.format(amount, 4);
```

---

## Complete Example

Here's a complete example showing how to use all the components together:

### DTO Class

```java
import jakarta.validation.constraints.*;
import zw.saas.validation.responses.validation.ValidEnum;
import zw.saas.validation.responses.enums.Gender;

public class StudentDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Gender is required")
    @ValidEnum(enumClass = Gender.class)
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}
```

### Service Class

```java
@Service
public class StudentService {

    public Student createStudent(StudentDto dto) {
        // Validate using GlobalFieldValidator
        GlobalFieldValidator.validateString(dto.getName(), "Name", 2, 50);
        GlobalFieldValidator.validateEmail(dto.getEmail(), "Email");
        GlobalFieldValidator.validatePhone(dto.getPhone(), "Phone");
        GlobalFieldValidator.validateDateOfBirth(dto.getDateOfBirth(), "Date of birth");

        // Check for duplicate
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Student with email " + dto.getEmail() + " already exists");
        }

        // Create and save student
        Student student = mapToEntity(dto);
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found with ID: " + id));
    }
}
```

### Controller Class

```java
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(
            @Valid @RequestBody StudentDto dto) {
        Student student = studentService.createStudent(dto);
        return ResponseEntityBuilder.created("Student created successfully", student);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudent(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        return ResponseEntityBuilder.ok("Student retrieved successfully", student);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntityBuilder.ok("Students retrieved successfully", students);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntityBuilder.ok("Student deleted successfully", null);
    }
}
```

### Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler extends zw.saas.validation.responses.exception.GlobalExceptionHandler {
    // All exceptions are handled automatically
}
```

---

## Project Information

- **Group ID**: zw.saas.validation
- **Artifact ID**: standard-api-response-business-utils
- **Version**: 1.0.0
- **Java Version**: 17+
- **Spring Boot Version**: 3.3.2

### Dependencies

- Spring Boot Starter Validation
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Lombok
- Hibernate Validator

---

## License

Apache License, Version 2.0

---

## Author

**Providence Chikukwa**
- Email: iamprovy@outlook.com
- GitHub: https://github.com/iamprovy-dev
- LinkedIn: https://www.linkedin.com/in/provychikukwa
- Organization: Classify
