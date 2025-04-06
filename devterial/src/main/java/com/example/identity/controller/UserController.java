package com.example.identity.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.identity.model.User;
import com.example.identity.services.UserService;

import jakarta.validation.Valid;

@RestController()
@RequestMapping(path = "/user")
public class UserController {
	@Autowired
	private UserService service;

	@PostMapping()
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest rq) {
		return ResponseEntity.ok(service.save(rq));
	}

	@GetMapping()
	public ResponseEntity<?> getAllUser() {
		return ResponseEntity.ok(service.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<GlobalResponse<UserRequest>> getUser(@PathVariable UUID id) {
		return ResponseEntity
				.ok(new GlobalResponse<UserRequest>(HttpStatus.BAD_REQUEST.value(), null, service.getOne(id)));
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<GlobalResponse<User>> putMethodName(@PathVariable UUID id, @RequestBody UserRequest entity) {
		//TODO: process PUT request
		User u = service.update(id, entity);
		return ResponseEntity.ok(new GlobalResponse<User>(200, "cập nhật thành công", u));
	}
	

}
