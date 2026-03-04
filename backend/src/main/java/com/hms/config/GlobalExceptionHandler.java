package com.hms.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> all(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getClass().getSimpleName(),
                "message", ex.getMessage()
        ));
    }
}
