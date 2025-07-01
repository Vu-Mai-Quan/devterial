package com.example.identity.exeptionsglobal;

import com.example.identity.enumvalue.StatusMessageEnum;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ControllerAdviceException {

    // Validate exception
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorModel> validateException(MethodArgumentNotValidException ex, HttpServletRequest rq) {
        List<String> errors = ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
        return ResponseEntity.badRequest().body(
                new ErrorModel(StatusMessageEnum.BAD_REQUEST, errors.toString(), rq.getRequestURI()));
    }

    @ExceptionHandler(value = NoResultException.class)
    public ResponseEntity<?> notFoundException(NoResultException run, HttpServletRequest rq) {
        return ResponseEntity.badRequest()
                .body(new ErrorModel(StatusMessageEnum.NOT_FOUND, run.getMessage(), rq.getRequestURI()));
    }


    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> unAuthorException(AccessDeniedException exception, HttpServletRequest rq) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorModel(StatusMessageEnum.FORBIDDEN, "Bạn không đủ quyên truy cập: " + exception.getMessage(), rq.getRequestURI()));
    }


}
