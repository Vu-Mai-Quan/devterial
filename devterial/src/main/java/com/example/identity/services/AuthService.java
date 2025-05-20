package com.example.identity.services;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.enumvalue.RoleEnum;

import java.util.List;
import java.util.UUID;

public interface AuthService {
	
	LoginResponse login(LoginRequest rq);

	String createAccessTokenByRefreshToken(String rftoken);

	void decentralizationUser(UUID id, List<RoleEnum> decentralizationRq);

}
