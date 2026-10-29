package co.edu.udistrital.udfy.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerUnitTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void sqlState23503Returns409() {
        SQLException sqlException = new SQLException("fk", "23503");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", sqlException);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(ex, request);
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void sqlState23505Returns409() {
        SQLException sqlException = new SQLException("dup", "23505");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", sqlException);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(ex, request);
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void sqlState23514Returns400() {
        SQLException sqlException = new SQLException("check", "23514");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", sqlException);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(ex, request);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void sqlState23502Returns400() {
        SQLException sqlException = new SQLException("not null", "23502");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", sqlException);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(ex, request);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void sqlState22003Returns400ConMensajeEnEspanol() {
        SQLException sqlException = new SQLException("out of range", "22003");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", sqlException);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(ex, request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Valor fuera de rango permitido", response.getBody().message());
    }

    @Test
    void sqlState22001Returns400ConMensajeEnEspanol() {
        SQLException sqlException = new SQLException("truncation", "22001");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", sqlException);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(ex, request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Valor de texto demasiado largo", response.getBody().message());
    }
}
