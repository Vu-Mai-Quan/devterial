package com.example.identity.controller;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.enumvalue.StatusMessageEnum;
import com.example.identity.services.AuthService;
import com.example.identity.services.BlackListTokenService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/auth/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;
    BlackListTokenService blackListTokenService;

    public record BlackListTokenRq(
            @Pattern(regexp = "^[A-Za-z0-9-_]+?\\.[A-Za-z0-9-_]+?\\.[A-Za-z0-9-_]+$", message = "Token không hợp lệ!") String token) {

    }


    @PostMapping(path = "public/login")
    public GlobalResponse<LoginResponse> isLogin(@RequestBody LoginRequest rq) {
        return new GlobalResponse<>(StatusMessageEnum.SUCCESS, StatusMessageEnum.SUCCESS.getMessage(), authService.login(rq));
    }

    @PostMapping("public/blacklisted-token")
    public ResponseEntity<GlobalResponse<?>> addBlackListToken(@RequestBody @Validated BlackListTokenRq rq) {
        try {
            var res = blackListTokenService.createBlackListToken(rq.token);
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.SUCCESS, StatusMessageEnum.SUCCESS.getMessage(), res));
        } catch (EntityExistsException | NoResultException | JwtException e) {
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.BAD_REQUEST, e.getMessage(), null));
        }

    }

    @PostMapping("public/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String rq) {
        try {
            var res = blackListTokenService.createBlackListToken(rq.substring(7));
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.SUCCESS, "Success", res));
        } catch (EntityExistsException | NoResultException e) {
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.BAD_REQUEST, e.getMessage(), null));
        }
    }

    public record RefreshTokenRequest(
            @Pattern(regexp = "^[A-Za-z0-9-_]+?\\.[A-Za-z0-9-_]+?\\.[A-Za-z0-9-_]+$", message = "Token refresh không hợp lệ!")
            @JsonProperty("token_refresh")
            String tokenRefresh) {
    }

    @PostMapping("public/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<?>> refreshToken(@RequestBody @Valid RefreshTokenRequest rq) {
        try {
            String newToken = authService.createAccessTokenByRefreshToken(rq.tokenRefresh());
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.SUCCESS, StatusMessageEnum.SUCCESS.getMessage(), String.format("{ access_token: %s }", newToken)));
        } catch (EntityNotFoundException | JwtException e) {
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.BAD_REQUEST, e.getMessage(), null));
        }
    }


    @PutMapping("decentralization/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponse<?>> decentralization(@PathVariable UUID id, @RequestBody List<RoleEnum> decentralizationRq) {
        try {
            authService.decentralizationUser(id, decentralizationRq);
            return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, "Phân quyền thành công", null));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.BAD_REQUEST, e.getMessage(), null));
        }

    }
}
