package com.example.identity.controller;

import java.util.UUID;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.services.BaseService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController()
@RequestMapping(path = "/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
	BaseService<UserRequest, UserResponse> service;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest rq) {
		return ResponseEntity.ok(service.save(rq));
	}

	@GetMapping()
	public ResponseEntity<?> getAllUser() {
		return ResponseEntity.ok(service.getAll());
	}

	@GetMapping("/get-user/{id}")
	public ResponseEntity<GlobalResponse<UserResponse>> getUser(@PathVariable UUID id) {
		return ResponseEntity
				.ok(new GlobalResponse<UserResponse>(HttpStatus.BAD_REQUEST.value(), null, service.getOne(id)));
	}

	@PutMapping("/update-user/{id}")
	public ResponseEntity<GlobalResponse<UserResponse>> putMethodName(@PathVariable UUID id,
			@RequestBody UserRequest entity) {
		// TODO: process PUT request
		try {
			UserResponse u = service.update(id, entity);
			return ResponseEntity.ok(new GlobalResponse<UserResponse>(HttpStatus.OK.value(), "cập nhật thành công", u));
		} catch (AuthenticationException e) {
			e.fillInStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GlobalResponse<UserResponse>(HttpStatus.UNAUTHORIZED.value(),
					String.format("cập nhật thất bại: %s", e.getMessage()), null));
		}

	}

}
