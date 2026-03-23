package zw.saas.validation.responses.configs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * Utility class for handling BigDecimal operations with null safety,
 * common calculations, and formatting.
 */
public final class BigDecimalUtils {

    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    private BigDecimalUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== VALIDATION ====================

    private static void validateInputs(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value == null) {
                throw new IllegalArgumentException("BigDecimal value cannot be null");
            }
        }
    }

    // ==================== NULL SAFETY ====================

    public static BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public static BigDecimal valueOrDefault(BigDecimal value, BigDecimal defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isNullOrNegative(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    // ==================== BASIC ARITHMETIC ====================

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        validateInputs(a, b);
        return a.add(b);
    }

    public static BigDecimal add(BigDecimal... values) {
        return Arrays.stream(values)
                .reduce(BigDecimal.ZERO, (result, next) -> {
                    validateInputs(next);
                    return result.add(next);
                });
    }

    public static BigDecimal sum(Collection<BigDecimal> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {
        validateInputs(minuend, subtrahend);
        return minuend.subtract(subtrahend);
    }

    public static BigDecimal subtract(BigDecimal first, BigDecimal... rest) {
        validateInputs(first);
        BigDecimal result = first;
        for (BigDecimal value : rest) {
            validateInputs(value);
            result = result.subtract(value);
        }
        return result;
    }

    public static BigDecimal subtractWithScale(BigDecimal minuend, BigDecimal subtrahend, int scale) {
        validateInputs(minuend, subtrahend);
        return minuend.subtract(subtrahend)
                .setScale(scale, DEFAULT_ROUNDING);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        validateInputs(a, b);
        return a.multiply(b);
    }

    public static BigDecimal multiply(BigDecimal... values) {
        return Arrays.stream(values)
                .reduce(BigDecimal.ONE, (result, next) -> {
                    validateInputs(next);
                    return result.multiply(next);
                });
    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        validateInputs(dividend, divisor);
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return dividend.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    public static BigDecimal divideWithScale(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
        validateInputs(dividend, divisor);
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return dividend.divide(divisor, scale, roundingMode);
    }

    // ==================== PERCENTAGE CALCULATIONS ====================

    public static BigDecimal percentageOf(BigDecimal value, BigDecimal percentage) {
        validateInputs(value, percentage);
        return value.multiply(percentage)
                .divide(new BigDecimal("100"), DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    public static BigDecimal increaseByPercentage(BigDecimal value, BigDecimal percentage) {
        return add(value, percentageOf(value, percentage));
    }

    public static BigDecimal decreaseByPercentage(BigDecimal value, BigDecimal percentage) {
        return subtract(value, percentageOf(value, percentage));
    }

    public static BigDecimal whatPercentage(BigDecimal a, BigDecimal b) {
        if (isNullOrZero(b)) {
            return BigDecimal.ZERO;
        }
        return divide(multiply(a, new BigDecimal("100")), b);
    }

    // ==================== TAX CALCULATIONS ====================

    public static BigDecimal calculateTax(BigDecimal value, BigDecimal taxRate) {
        return percentageOf(value, taxRate);
    }

    public static BigDecimal addTax(BigDecimal value, BigDecimal taxRate) {
        return add(value, calculateTax(value, taxRate));
    }

    public static BigDecimal removeTax(BigDecimal gross, BigDecimal taxRate) {
        BigDecimal divisor = BigDecimal.ONE.add(divide(taxRate, new BigDecimal("100")));
        return divide(gross, divisor);
    }

    // ==================== DISCOUNT CALCULATIONS ====================

    public static BigDecimal applyDiscount(BigDecimal value, BigDecimal discountPercentage) {
        return decreaseByPercentage(value, discountPercentage);
    }

    public static BigDecimal discountAmount(BigDecimal value, BigDecimal discountPercentage) {
        return percentageOf(value, discountPercentage);
    }

    // ==================== COMPARISON ====================

    public static int compare(BigDecimal a, BigDecimal b) {
        validateInputs(a, b);
        return a.compareTo(b);
    }

    public static boolean isGreaterThan(BigDecimal a, BigDecimal b) {
        return compare(a, b) > 0;
    }

    public static boolean isGreaterThanOrEqual(BigDecimal a, BigDecimal b) {
        return compare(a, b) >= 0;
    }

    public static boolean isLessThan(BigDecimal a, BigDecimal b) {
        return compare(a, b) < 0;
    }

    public static boolean isLessThanOrEqual(BigDecimal a, BigDecimal b) {
        return compare(a, b) <= 0;
    }

    public static boolean isEqual(BigDecimal a, BigDecimal b) {
        return compare(a, b) == 0;
    }

    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        return isLessThan(a, b) ? a : b;
    }

    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        return isGreaterThan(a, b) ? a : b;
    }

    // ==================== ENTITY HELPER METHODS (for Transaction entity) ====================

    public static BigDecimal calculateBalance(BigDecimal debit, BigDecimal credit) {
        return valueOrZero(debit).subtract(valueOrZero(credit));
    }

    public static boolean isDebitGreaterThanCredit(BigDecimal debit, BigDecimal credit) {
        return calculateBalance(debit, credit).compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isCreditGreaterThanDebit(BigDecimal debit, BigDecimal credit) {
        return calculateBalance(debit, credit).compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isBalanced(BigDecimal debit, BigDecimal credit) {
        return calculateBalance(debit, credit).compareTo(BigDecimal.ZERO) == 0;
    }

    public static BigDecimal getAbsoluteBalance(BigDecimal debit, BigDecimal credit) {
        return calculateBalance(debit, credit).abs();
    }

    public static BigDecimal getBalanceWithScale(BigDecimal debit, BigDecimal credit, int scale) {
        return calculateBalance(debit, credit).setScale(scale, DEFAULT_ROUNDING);
    }

    public static void validateAmounts(BigDecimal debit, BigDecimal credit) {
        if (debit != null && debit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Debit cannot be negative");
        }
        if (credit != null && credit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Credit cannot be negative");
        }
    }

    // ==================== AGGREGATION ====================

    public static BigDecimal totalExpenses(List<BigDecimal> expenses) {
        return sum(expenses);
    }

    public static BigDecimal calculateNetIncome(BigDecimal income, List<BigDecimal> expenses) {
        return subtract(income, totalExpenses(expenses));
    }

    public static BigDecimal average(Collection<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return divide(sum(values), new BigDecimal(values.size()));
    }

    public static BigDecimal sumOfProducts(List<BigDecimal> quantities, List<BigDecimal> prices) {
        if (quantities.size() != prices.size()) {
            throw new IllegalArgumentException("Lists must have same size");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < quantities.size(); i++) {
            sum = sum.add(multiply(quantities.get(i), prices.get(i)));
        }
        return sum;
    }

    // ==================== STREAM OPERATIONS ====================

    public static BinaryOperator<BigDecimal> sumOperator() {
        return BigDecimal::add;
    }

    public static BinaryOperator<BigDecimal> multiplyOperator() {
        return BigDecimal::multiply;
    }

    public static BinaryOperator<BigDecimal> maxOperator() {
        return (a, b) -> a.compareTo(b) > 0 ? a : b;
    }

    public static BinaryOperator<BigDecimal> minOperator() {
        return (a, b) -> a.compareTo(b) < 0 ? a : b;
    }

    // ==================== FORMATTING ====================

    public static String format(BigDecimal value) {
        return format(value, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    public static String format(BigDecimal value, int scale) {
        return format(value, scale, DEFAULT_ROUNDING);
    }

    public static String format(BigDecimal value, int scale, RoundingMode roundingMode) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(scale, roundingMode).toString();
    }

    public static String formatAsCurrency(BigDecimal value) {
        return format(value, 2, RoundingMode.HALF_UP);
    }

    public static String formatAsPercentage(BigDecimal value) {
        if (value == null) {
            return "0.00%";
        }
        return multiply(value, new BigDecimal("100")).setScale(2, DEFAULT_ROUNDING) + "%";
    }

    // ==================== PARSING ====================

    public static BigDecimal parse(String value) {
        return parse(value, BigDecimal.ZERO);
    }

    public static BigDecimal parse(String value, BigDecimal defaultValue) {
        try {
            return value != null && !value.trim().isEmpty() ? new BigDecimal(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // ==================== UTILITY ====================

    public static BigDecimal round(BigDecimal value, int scale) {
        return round(value, scale, DEFAULT_ROUNDING);
    }

    public static BigDecimal round(BigDecimal value, int scale, RoundingMode roundingMode) {
        validateInputs(value);
        return value.setScale(scale, roundingMode);
    }

    public static BigDecimal abs(BigDecimal value) {
        validateInputs(value);
        return value.abs();
    }

    public static BigDecimal negate(BigDecimal value) {
        validateInputs(value);
        return value.negate();
    }

    public static BigDecimal zeroIfNegative(BigDecimal value) {
        return isNullOrNegative(value) ? BigDecimal.ZERO : value;
    }
}