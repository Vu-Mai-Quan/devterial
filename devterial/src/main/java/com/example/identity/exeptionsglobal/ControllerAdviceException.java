package com.example.identity.exeptionsglobal;

import com.example.identity.dto.response.GlobalResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ControllerAdviceException {

    private record ErrorResponse<T>(HttpStatus status, T res, LocalDateTime time) {
    }

    // Validate exception
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<Map<String, String>>>> validateException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> mapError = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    assert error.getDefaultMessage() != null;
                    return Map.of(error.getField(), error.getDefaultMessage());
                }).toList();
        return ResponseEntity.badRequest().body(
                new ErrorResponse<List<Map<String, String>>>(HttpStatus.BAD_REQUEST, mapError, LocalDateTime.now()));
    }

    @ExceptionHandler(value = NoResultException.class)
    public ResponseEntity<?> notFoundException(NoResultException run) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse<String>(HttpStatus.BAD_REQUEST, run.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<?> handleConstraintViolation(JpaSystemException ex) {
        final String defaultMessage = "Dữ liệu đã tồn tại. Vui lòng kiểm tra lại thông tin.";
        String message = defaultMessage;
        Throwable rootCause = ex.getRootCause();
        if (rootCause != null && rootCause.getMessage() != null) {
            String rootMsg = rootCause.getMessage();
            if (rootMsg.contains("UNIQUE constraint failed")) {
                // SQLite trả về lỗi dạng: UNIQUE constraint failed: user.username
                int idx = rootMsg.lastIndexOf('.');
                if (idx != -1) {
                    String field = rootMsg.substring(idx + 1);
                    message = String.format("Dữ liệu '%s' đã tồn tại. Vui lòng chọn giá trị khác.", field);
                }
            }
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse<String>(HttpStatus.BAD_REQUEST, message, LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity unAuthorException(AccessDeniedException exception) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN, "Bạn không đủ quyên truy cập: " + exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<GlobalResponse<String>> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GlobalResponse<>(HttpStatus.BAD_REQUEST.value(), "Token đã hết hạn", ex.getMessage()));
    }

    @ExceptionHandler({MalformedJwtException.class, UnsupportedJwtException.class})
    public ResponseEntity<GlobalResponse<String>> handleMalformedOrUnsupportedJwtException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GlobalResponse<>(HttpStatus.BAD_REQUEST.value(), "Token không đúng định dạng", ex.getMessage()));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<GlobalResponse<String>> handleSignatureException(SignatureException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GlobalResponse<>(HttpStatus.BAD_REQUEST.value(), "Token không hợp lệ", ex.getMessage()));
    }
}
