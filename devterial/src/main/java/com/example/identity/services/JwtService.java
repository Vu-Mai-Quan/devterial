package com.example.identity.services;

import com.example.identity.dto.request.UserAuthorRq;
import com.example.identity.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {
    String extractUserName(String jwt);

    String createToken(UserAuthorRq userAuthorRq);

    boolean isInssuerValid(String token);

    Claims decodeToken(String token);

    String createRefreshToken(User user);

    boolean isBlackList(String jwt);

    boolean validateToken(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);


    String getLastRefreshTokenFromDataBase(UUID id);
}
