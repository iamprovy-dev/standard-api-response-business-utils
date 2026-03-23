package zw.saas.validation.responses.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Email;

public class EmailValidator implements ConstraintValidator<Email, String> {

    public boolean isValid(String email, ConstraintValidatorContext context){
        return email.matches("^[a-z0-9._%s+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$");
    }
}
