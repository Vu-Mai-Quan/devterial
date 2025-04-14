package com.example.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.services.AuthService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController()
@RequestMapping(path = "/auth/")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
	AuthService authService;
	@PostMapping(path = "login")
	public GlobalResponse<LoginResponse> isLogin(@RequestBody LoginRequest rq) {
		return new GlobalResponse<LoginResponse>(HttpStatus.OK.value(), null, authService.login(rq));
	}

}
