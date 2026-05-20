package com.momo.expense_tracker.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RessourceNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleRessourceNotFound(
      RessourceNotFoundException ex) {
    Map<String, String> body = Map.of("error", "Not Found", "message", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidMethodArgument(
      MethodArgumentNotValidException ex) {

    Map<String, String> fieldErrors = new HashMap<>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    Map<String, Object> body = Map.of("error", "Validation Failed", "details", fieldErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {

    Map<String, String> body =
        Map.of("error", "Http message is not readable", "message", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
      IllegalArgumentException ex) {

    Map<String, String> body =
        Map.of("error", "Illegal argument entered", "message", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }
}
