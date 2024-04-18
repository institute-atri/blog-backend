package org.instituteatri.backendblog.infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@ControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Object> handleAccountLockedException(AccountLockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", ex.getMessage()));
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", ex.getMessage()));
    }
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Object> handleTagNotFoundException(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("message", ex.getMessage()));
    }
    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<Object> handleNotAuthenticatedException(NotAuthenticatedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Object> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(DomainAccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(DomainAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", ex.getMessage()));
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<Object> handleCustomAuthenticationException(CustomAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Initialize a map to store validation errors
        Map<String, String> errors = new HashMap<>();

        // Iterate over the validation errors found
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            // Get the name of the field that caused the validation error
            String fieldName = error.getField();
            // Get the standard error message associated with the validation error
            String errorMessage = error.getDefaultMessage();

            // Check if the field has had a validation error before
            if (!errors.containsKey(fieldName)) {
                // If the field has not yet been validated, add the error message associated with it to the error map
                errors.put(fieldName, errorMessage);
            }
        });
        // Return an HTTP 400 Bad Request response containing the validation error map
        return ResponseEntity.badRequest().body(errors);
    }
}
