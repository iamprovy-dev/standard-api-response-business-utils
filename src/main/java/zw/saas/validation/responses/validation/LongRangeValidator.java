package zw.saas.validation.responses.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LongRangeValidator implements ConstraintValidator<ValidLongRange, Long> {

    private long min;
    private long max;

    @Override
    public void initialize(ValidLongRange constraint) {
        this.min = constraint.min();
        this.max = constraint.max();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext ctx) {
        if (value == null) return true; // use @NotNull if required
        return value >= min && value <= max;
    }
}
