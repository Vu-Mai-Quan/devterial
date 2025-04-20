package com.example.identity.controller;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.services.AuthService;
import com.example.identity.services.BlackListTokenService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController()
@RequestMapping(path = "/auth/public/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;
    BlackListTokenService blackListTokenService;


    public record BlackListTokenRq(@Pattern(regexp = "^[A-Za-z0-9-_]+?\\.[A-Za-z0-9-_]+?\\.[A-Za-z0-9-_]+$", message = "Token không hợp lệ!") String token){

    }
    @PostMapping(path = "login")
    public GlobalResponse<LoginResponse> isLogin(@RequestBody LoginRequest rq) {
        return new GlobalResponse<LoginResponse>(HttpStatus.OK.value(), null, authService.login(rq));
    }

    @PostMapping("blacklisted-token")
    public ResponseEntity addBlackListToken(@RequestBody @Validated BlackListTokenRq rq) {
        try {
            var res = blackListTokenService.createBlackListToken(rq.token);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (EntityExistsException | NoResultException e) {
            return ResponseEntity.badRequest().body(new GlobalResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(),null));
        }

    }
}
