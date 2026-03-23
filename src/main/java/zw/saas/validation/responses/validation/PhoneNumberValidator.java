package zw.saas.validation.responses.validation;

import java.util.regex.Pattern;

public class PhoneNumberValidator {
    private static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9]{1,3}?[-.\\s]?[0-9]{3}[-.\\s]?[0-9]{3}[-.\\s]?[0-9]{4}$";

    public boolean isValid(String phoneNumber){
        return Pattern.matches(PHONE_NUMBER_PATTERN,phoneNumber);
    }
}
