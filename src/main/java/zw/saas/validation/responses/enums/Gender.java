package zw.saas.validation.responses.enums;

public enum Gender {
    MALE("Male gender, typically associated with masculinity."),
    FEMALE("Female gender, typically associated with femininity.");

    private final String description;

    // Constructor to set the description for each enum constant
    Gender(String description) {
        this.description = description;
    }

    // Getter method to retrieve the description of each enum constant
    public String getDescription() {
        return description;
    }
}
