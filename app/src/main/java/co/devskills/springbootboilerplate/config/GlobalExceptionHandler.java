package co.devskills.springbootboilerplate.config;

import co.devskills.springbootboilerplate.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        log.debug("Validation failed", e);
        return ResponseEntity.badRequest().body("Mandatory body parameters missing or have incorrect type.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.debug("Message not readable", e);
        return ResponseEntity.badRequest().body("Mandatory body parameters missing or have incorrect type.");
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException e) {
        log.debug("item not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getResponseText());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        log.error("Invalid request", e);
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String message = "Invalid request";
        if (path.contains("/transactions/")) {
            message = "transaction_id missing or has incorrect type.";
        } else if (path.contains("/accounts/")) {
            message = "account_id missing or has incorrect type.";
        }
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.debug("Method not allowed", e);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Specified HTTP method not allowed.");
    }
}
