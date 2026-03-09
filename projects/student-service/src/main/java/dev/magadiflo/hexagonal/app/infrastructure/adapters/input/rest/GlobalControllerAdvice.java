package dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest;

import dev.magadiflo.hexagonal.app.domain.exception.StudentNotFoundException;
import dev.magadiflo.hexagonal.app.domain.model.ErrorResponse;
import dev.magadiflo.hexagonal.app.utils.ErrorCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCatalog.STUDENT_NOT_FOUND.getCode())
                .message(ErrorCatalog.STUDENT_NOT_FOUND.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List<String> fieldErrorsList = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCatalog.INVALID_STUDENT.getCode())
                .message(ErrorCatalog.INVALID_STUDENT.getMessage())
                .details(fieldErrorsList)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCatalog.GENERIC_ERROR.getCode())
                .message(ErrorCatalog.GENERIC_ERROR.getMessage())
                .details(Collections.singletonList(exception.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}