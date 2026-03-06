package dev.magadiflo.banking.app.shared.exception;

import dev.magadiflo.banking.app.account.domain.exception.AccountBlockedException;
import dev.magadiflo.banking.app.account.domain.exception.AccountClosedException;
import dev.magadiflo.banking.app.account.domain.exception.AccountInactiveException;
import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.exception.InsufficientFundsException;
import dev.magadiflo.banking.app.account.domain.exception.InvalidAmountException;
import dev.magadiflo.banking.app.account.domain.exception.MaxAccountsReachedException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerAlreadyExistsException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerBlockedException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerInactiveException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.transaction.domain.exception.TransactionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — NOT FOUND
    // =========================================================
    @ExceptionHandler({
            CustomerNotFoundException.class,
            AccountNotFoundException.class,
            TransactionNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(Exception e, HttpServletRequest request) {
        log.error("{}", e.getMessage());
        return this.buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

    // 409 — CONFLICT
    // =========================================================
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(Exception e, HttpServletRequest request) {
        log.error("{}", e.getMessage());
        return this.buildResponse(HttpStatus.CONFLICT, e.getMessage(), request.getRequestURI());
    }

    // 422 — UNPROCESSABLE CONTENT
    // =========================================================
    @ExceptionHandler({
            CustomerBlockedException.class,
            CustomerInactiveException.class,
            AccountBlockedException.class,
            AccountClosedException.class,
            AccountInactiveException.class,
            InsufficientFundsException.class,
            MaxAccountsReachedException.class,
            InvalidAmountException.class
    })
    public ResponseEntity<ErrorResponse> handleUnprocessable(Exception e, HttpServletRequest request) {
        log.error("{}", e.getMessage());
        return this.buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage(), request.getRequestURI());
    }

    // 400 — BAD REQUEST (Bean Validation)
    // =========================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("{}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación",
                errors.toString(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 502 — BAD GATEWAY (fallo en API externa)
    // =========================================================
    @ExceptionHandler(ExchangeRateUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBadGateway(Exception e, HttpServletRequest request) {
        log.error("{}", e.getMessage());
        return this.buildResponse(HttpStatus.BAD_GATEWAY, e.getMessage(), request.getRequestURI());
    }

    // 500 — INTERNAL SERVER ERROR (genérico)
    // =========================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
        log.error("{}", e.getMessage());
        return this.buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Por favor intente nuevamente.",
                request.getRequestURI()
        );
    }

    // Method de apoyo
    // =========================================================
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        var response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(status).body(response);
    }
}
