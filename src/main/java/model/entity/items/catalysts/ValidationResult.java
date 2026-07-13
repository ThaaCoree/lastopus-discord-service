package model.entity.items.catalysts;

public class ValidationResult {
    private final boolean success;
    private final String message;

    public ValidationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }

    public String getMessage() {
        return message;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, "");
    }

    public static ValidationResult success(String message) {
        return new ValidationResult(true, message);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }
}