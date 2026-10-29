package co.edu.udistrital.udfy.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        Throwable root = getRootCause(ex);
        if (root instanceof SQLException sqlException) {
            String sqlState = sqlException.getSQLState();
            if (sqlState != null) {
                if ("23505".equals(sqlState)) {
                    return buildError(HttpStatus.CONFLICT, "Duplicate entry", request);
                }
                if ("23503".equals(sqlState)) {
                    return buildError(HttpStatus.CONFLICT, "Reference or dependent record violation", request);
                }
                if ("23514".equals(sqlState)) {
                    return buildError(HttpStatus.BAD_REQUEST, "Check constraint violation", request);
                }
                if ("23502".equals(sqlState)) {
                    return buildError(HttpStatus.BAD_REQUEST, "Not null constraint violation", request);
                }
                if ("P0001".equals(sqlState)) {
                    return buildError(HttpStatus.BAD_REQUEST, "Trigger violation", request);
                }
                if ("22003".equals(sqlState)) {
                    return buildError(HttpStatus.BAD_REQUEST, "Valor fuera de rango permitido", request);
                }
                if ("22001".equals(sqlState)) {
                    return buildError(HttpStatus.BAD_REQUEST, "Valor de texto demasiado largo", request);
                }
                if ("42501".equals(sqlState)) {
                    return buildError(HttpStatus.FORBIDDEN, "Insufficient privileges", request);
                }
            }
        }
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid credentials", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid credentials", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida";
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(org.springframework.web.server.ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        ApiError error = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, HttpServletRequest request) {
        ApiError error = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }
}
