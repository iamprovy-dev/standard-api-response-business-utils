package zw.saas.validation.responses.validation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import zw.saas.validation.responses.collections.Contacts;
import zw.saas.validation.responses.collections.Guardian;
import zw.saas.validation.responses.enums.Qualifications;
import zw.saas.validation.responses.enums.Relationship;
import zw.saas.validation.responses.exception.MissingFieldsException;
import zw.saas.validation.responses.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class GlobalFieldValidator {

    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    public static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]?[0-9\\s\\-()]{10,15}$");
    public static final Pattern URL_PATTERN =
            Pattern.compile(
                    "^(https?://)" +                  // Must start with http:// or https://
                            "([\\w.-]+|localhost|\\d{1,3}(\\.\\d{1,3}){3})" + // Domain, localhost, or IPv4
                            "(:\\d+)?(/.*)?$"                 // Optional port and path
            );
    // Optional path


    public static void validateString(String value, String fieldName, int minLength, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new MissingFieldsException(fieldName + " is required");
        }

        String trimmed = value.trim();
        if (trimmed.length() < minLength) {
            throw new ValidationException(
                    fieldName + " must be at least " + minLength + " characters long"
            );
        }

        if (trimmed.length() > maxLength) {
            throw new ValidationException(
                    fieldName + " must not exceed " + maxLength + " characters"
            );
        }
    }

    public static void validateEmail(String email, String fieldName) {
        validateString(email, fieldName, 5, 100);

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName + " has invalid format");
        }
    }


    public static void validatePhone(String phone, String fieldName) {
        validateString(phone, fieldName, 10, 15);

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(fieldName + " has invalid format");
        }
    }

    public static void validateLocalDate(LocalDate date, String fieldName) {
        if (date == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    public static void validatePaymentDate(LocalDate paymentDate, String fieldName) {
        validateLocalDate(paymentDate, fieldName);

        if (paymentDate.isAfter(LocalDate.now())) {
            throw new ValidationException(fieldName + " cannot be after today");
        }
    }

    public static void validateDatesBeforeToday(LocalDate localDate, String fieldName) {
        validateLocalDate(localDate, fieldName);

        if (localDate.isBefore(LocalDate.now())) {
            throw new ValidationException(String.format("%s cannot be any day before today", fieldName));
        }
    }

    public static void validateDatesBeforeAnotherDates(LocalDate firstDate, LocalDate lastDate, String fieldName) {
        validateLocalDate(lastDate, fieldName);

        if (lastDate.isBefore(firstDate)) {
            throw new ValidationException(String.format("%s cannot be before opening date (%s)", fieldName, firstDate));
        }
    }

    public static void validateOpeningDate(LocalDate openingDate, String fieldName) {
        validateLocalDate(openingDate, fieldName);


        if (openingDate.isBefore(LocalDate.now().minusDays(7))) {
            throw new ValidationException(String.format(" %s cannot be before %s", fieldName, LocalDate.now().minusDays(7)));
        }
    }

    public static void validateDateOfBirth(LocalDate dobValue, String fieldName) {
        validateLocalDate(dobValue, fieldName);

        Period period = Period.between(dobValue, LocalDate.now());

        if (period.getYears() < 2) {
            throw new ValidationException(" must have at least 2 years of age");
        }

    }


    public static void validateUrl(String url, String fieldName) {
        validateString(url, fieldName, 7, 2083); // min length 7 for "http://a.b"
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new ValidationException(fieldName + " must start with http:// or https:// and be a valid URL");
        }
    }


    // Validate positive Long ID
    public static void validatePositiveLong(Long value, String fieldName) {
        validateNonNull(value, fieldName);

        if (value <= 0) {
            throw new ValidationException(fieldName + " must be a positive number");
        }
    }

    // Validate list is not empty
    public static void validateNonEmptyList(List<?> list, String fieldName) {
        validateNonNull(list, fieldName);

        if (list.isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static <T extends Enum<T>> void validateEnum(Class<T> enumClass, T value, String fieldName) {
        validateNonNull(value, fieldName);

        try {
            // Ensure it's a valid enum value
            Enum.valueOf(enumClass, value.name());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid " + fieldName + " value: " + value +
                            ". Valid values are: " + getEnumNames(enumClass)
            );
        }
    }

    public static <T extends Enum<T>> String getEnumNames(Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        String[] names = new String[enumConstants.length];
        for (int i = 0; i < enumConstants.length; i++) {
            names[i] = enumConstants[i].name();
        }
        return String.join(", ", names);
    }

    // Validate money amount (positive decimal)
    public static void validatePositiveAmount(BigDecimal amount, String fieldName) {
        validateNonNull(amount, fieldName);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero");
        }
    }

    // Validate money amount with minimum value
    public static void validateAmount(BigDecimal amount, String fieldName, BigDecimal minValue) {
        validateNonNull(amount, fieldName);

        if (amount.compareTo(minValue) < 0) {
            throw new ValidationException(fieldName + " must be at least " + minValue);
        }
    }

    // Validate percentage (0-100)
    public static void validatePercentage(BigDecimal percentage, String fieldName) {
        validateNonNull(percentage, fieldName);

        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException(fieldName + " must be between 0 and 100");
        }
    }

    // Validate boolean value
    public static void validateBoolean(Boolean value, String fieldName) {
        validateNonNull(value, fieldName);
    }

    public static void validateUUID(String value, String fieldName) {
        validateNonNull(value, fieldName);
        if (value.length() != 36) {
            throw new ValidationException(String.format("Invalid %s value: ", fieldName));
        }
    }

    // Validate Qualifications enum
    public static void validateQualification(Qualifications qualification) {
        validateNonNull(qualification, "Qualification");

        try {
            Qualifications.valueOf(qualification.name());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid qualification value: " + qualification);
        }
    }

    public static void validateNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new MissingFieldsException(fieldName + " is required");
        }
    }

    public static void validateContact(Contacts contact, int index) {
        String contactPrefix = "Contact " + (index + 1);

        // Validate contact person
        validateString(contact.getContactPerson(), contactPrefix + " person", 2, 100);

        validatePhone(contact.getContactNumber(), contactPrefix +" number");

        // Validate email address if provided
        if (contact.getEmailAddress() != null && !contact.getEmailAddress().isBlank()) {
            validateEmail(contact.getEmailAddress(), contactPrefix +" email");

        }

        // Validate designation if provided
        if (contact.getDesignation() != null && !contact.getDesignation().isBlank()) {
            GlobalFieldValidator.validateString(contact.getDesignation(), contactPrefix + " designation", 2, 100);
        }
    }

    public static void validateInteger(int value, String variableName) {
        if (value < 0) {
            throw new ValidationException(variableName + " cannot be negative");
        }

    }

    public static void validatePeriod(int period, String periodName) {
        if (period < 0) {
            throw new ValidationException(periodName + " cannot be negative");
        }

        // Set reasonable maximum limits for periods (e.g., 365 days = 1 year)
        if (period > 365) {
            throw new ValidationException(periodName + " cannot exceed 365 days");
        }
    }

    public static void validateSizeLimit(int size, String sizeName) {
        if (size <= 0) {
            throw new ValidationException(sizeName + " must be greater than zero");
        }

        // Set reasonable maximum for size limits (e.g., 1,000,000 users/items)
        if (size > 1000000) {
            throw new ValidationException(sizeName + " is too large, maximum allowed is 1,000,000");
        }
    }

    public static void validateFee(double fee, String feeName) {
        if (fee < 0) {
            throw new ValidationException(feeName + " cannot be negative");
        }

        // Validate fee precision (optional - max 2 decimal places for currency)
        String feeStr = String.valueOf(fee);
        if (feeStr.contains(".") && feeStr.split("\\.")[1].length() > 2) {
            throw new ValidationException(feeName + " should have at most 2 decimal places");
        }
    }

    public static void validateGuardian(Guardian guardian){
        validateString(guardian.getName(), "Student guardian name", 3, 30);
        validateString(guardian.getSurname(), "Student guardian surname", 3, 30);
        validatePhone(guardian.getMobile(), "Student guardian mobile number");
        validateString(guardian.getAddress(), "Student guardian address", 3, 100);
        validateEnum(Relationship.class, guardian.getRelationship(),"Student guardian relationship");
    }
}