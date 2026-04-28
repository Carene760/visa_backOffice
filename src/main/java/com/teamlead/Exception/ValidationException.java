package com.teamlead.Exception;

import java.util.*;

public class ValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    private final List<String> errors;

    public ValidationException(String message) {
        this(message, (Throwable) null);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = new ArrayList<>();
    }

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors == null ? new ArrayList<>() : new ArrayList<>(errors);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
