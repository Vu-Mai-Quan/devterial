package com.example.identity.services.imlp;

import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.model.BlackListToken;
import com.example.identity.repositories.BlackListRepositories;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.services.BlackListTokenService;
import com.example.identity.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlackListTokenImlp implements BlackListTokenService {

    BlackListRepositories blackListRepositories;
    JwtService jwtService;
    JpaRepositoriyUser jpaRepositoriyUser;

    /**
     * Tạo ra một token mới
     */
    @Override
    @Transactional
    public GlobalResponse<?> createBlackListToken(String token) {
        /*Kiểm tra coi token hợp lệ không*/
        if (!jwtService.isInssuValid(token)) {
            return new GlobalResponse<String>(HttpStatus.BAD_REQUEST.value(), "Token không hợp lệ", null);
        }
        Optional<BlackListToken> blackListToken = blackListRepositories.findByToken(token);
        if (blackListToken.isPresent()) {
            throw new EntityExistsException("Token đã tồn tại trong danh sách đen");
        } else {
            var clams = jwtService.decodeToken(token);
            var user = jpaRepositoriyUser.findById(UUID.fromString( clams.get("id", String.class))).orElseThrow(() -> new NoResultException(String.format("Không có user với id: %s", clams.getId())));
            Date date = clams.getExpiration();
            BlackListToken blackList = new BlackListToken();
            blackList.setUser(user);
            blackList.setToken(token);
            blackList.setExpiredDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            return new GlobalResponse<>(HttpStatus.OK.value(), "Token đã thêm vào blacklisted", blackListRepositories.save(blackList));
        }

    }


}

