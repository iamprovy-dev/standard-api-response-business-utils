package zw.saas.validation.responses.enums;

public enum PayFrequency {
    WEEKLY("Seven(7) days Period"),
    FORTNIGHT("Fourteen(14) days Period"),
    MONTHLY("Twenty-eight(28) days Period"),
    NOT_APPLICABLE("Normally used by government institutions whose employees are paid by the government or external entities");

    private final String description;

    // Constructor to set the description for each enum constant
    PayFrequency(String description) {
        this.description = description;
    }

    // Getter method to retrieve the description of each enum constant
    public String getDescription() {
        return description;
    }
}
