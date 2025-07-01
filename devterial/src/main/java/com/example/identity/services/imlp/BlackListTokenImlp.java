package com.example.identity.services.imlp;

import com.example.identity.model.BlackListToken;
import com.example.identity.model.User;
import com.example.identity.repositories.BlackListRepositories;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.services.BlackListTokenService;
import com.example.identity.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
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
    public String createBlackListToken(String token) {
        /*Kiểm tra coi token hợp lệ không*/
        try {
            if (!jwtService.isInssuerValid(token)) {
                return "Token không hợp lệ";
            }
            if (blackListRepositories.existsById(token)) {
                throw new EntityExistsException("Token đã tồn tại trong danh sách đen");
            } else {
                saveBlackListToken(token);
                return "Token đã thêm vào blacklisted";
            }

        } catch (ExpiredJwtException e) {
            throw new JwtException("Token đã hết hạn không cần khóa", e);
        }


    }

    @Transactional
    protected void saveBlackListToken(String token) {

        Claims clams = jwtService.decodeToken(token);
        var idUser = UUID.fromString(clams.get("id", String.class));
        boolean bool = jpaRepositoriyUser.existsById(idUser);
        if (bool) {
            Date date = clams.getExpiration();
            BlackListToken blackList = new BlackListToken();
            blackList.setUser(User.builder().id(idUser).build());
            blackList.setToken(token);
            blackList.setExpiredDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            blackListRepositories.save(blackList);
        }
        throw new NoResultException(String.format("Không có user với id: %s", idUser));
    }


}

