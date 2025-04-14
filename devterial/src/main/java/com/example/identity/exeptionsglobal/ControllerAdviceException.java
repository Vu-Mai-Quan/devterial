package com.example.identity.exeptionsglobal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.NoResultException;

@ControllerAdvice
public class ControllerAdviceException {

	private record ErrorResponse<T>(HttpStatus status, T res, LocalDateTime time) {
	}

	// Validate exception
		@ExceptionHandler(value = MethodArgumentNotValidException.class)
		public ResponseEntity<ErrorResponse<List<Map<String, String>>>> validateException(MethodArgumentNotValidException ex) {
			List<Map<String, String>> mapError = ex.getBindingResult().getFieldErrors().stream()
					.map(error -> Map.of(error.getField(), error.getDefaultMessage())).toList();
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

	
}
