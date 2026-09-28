package com.guesstheworld.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? Collections.unmodifiableList(errors) : Collections.emptyList();
    }

    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    public static ValidationResult failure(String singleError) {
        List<String> list = new ArrayList<>();
        list.add(singleError);
        return new ValidationResult(false, list);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        return errors.isEmpty() ? "" : errors.get(0);
    }

    public String getFormattedErrors() {
        return String.join("\n• ", errors);
    }
}
