package com.example.identity.services;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.response.LoginResponse;

public interface AuthService {
	LoginResponse login(LoginRequest rq);
	boolean isAuthencate(String token);
}
