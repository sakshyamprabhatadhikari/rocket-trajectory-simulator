package util;

public class Validator {

    // Guideline year message style
    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 2025;

    public static String validateRocketID(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "Rocket ID cannot be empty.";
        }
        return null;
    }

    public static String validateRocketName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Rocket Name cannot be empty.";
        }
        return null;
    }

    public static Integer parseAndValidateLaunchYear(String yearText) throws NumberFormatException {
        int year = Integer.parseInt(yearText.trim());
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException("Launch Year must be a number between " + MIN_YEAR + " and " + MAX_YEAR + ".");
        }
        return year;
    }

    public static Double parseAndValidateNonNegativeDouble(String text, String fieldName) throws NumberFormatException {
        double value = Double.parseDouble(text.trim());
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
        return value;
    }

    public static String validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Status must be selected.";
        }
        // Must be one of the allowed statuses you already use
        if (!(status.equalsIgnoreCase("Active") ||
              status.equalsIgnoreCase("Inactive") ||
              status.equalsIgnoreCase("Testing"))) {
            return "Status must be Active, Inactive, or Testing.";
        }
        return null;
    }
}

