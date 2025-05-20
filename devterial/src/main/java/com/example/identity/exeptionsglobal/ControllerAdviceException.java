package com.example.identity.exeptionsglobal;

import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdviceException {

    public record ErrorResponse<T>(HttpStatus status, T res, LocalDateTime time) {

        public ErrorResponse(HttpStatus status, T res) {
            this(status, res, LocalDateTime.now());
        }
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
                new ErrorResponse<>(HttpStatus.BAD_REQUEST, mapError));
    }

    @ExceptionHandler(value = NoResultException.class)
    public ResponseEntity<?> notFoundException(NoResultException run) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse<>(HttpStatus.BAD_REQUEST, run.getMessage()));
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<?> handleConstraintViolation(JpaSystemException ex) {
        String message = "Dữ liệu đã tồn tại. Vui lòng kiểm tra lại thông tin.";
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
                .body(new ErrorResponse<>(HttpStatus.BAD_REQUEST, message));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> unAuthorException(AccessDeniedException exception) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse<>(HttpStatus.FORBIDDEN, "Bạn không đủ quyên truy cập: " + exception.getMessage()));
    }



}
