package bridge.bridge_bank.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException e, HttpServletRequest request) {
        log.error("NOT_FOUND at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("NOT_FOUND", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(
            PasswordMismatchException e, HttpServletRequest request) {
        log.error("UNAUTHORIZED at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("UNAUTHORIZED", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        log.error("FORBIDDEN at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("FORBIDDEN", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(
            InsufficientBalanceException e, HttpServletRequest request) {
        log.error("INSUFFICIENT_BALANCE at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("INSUFFICIENT_BALANCE", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        log.error("BAD_REQUEST at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("BAD_REQUEST", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("INTERNAL_SERVER_ERROR at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("INTERNAL_SERVER_ERROR", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
