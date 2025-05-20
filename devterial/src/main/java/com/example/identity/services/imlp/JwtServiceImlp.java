package com.example.identity.services.imlp;

import com.example.identity.dto.request.UserAuthorRq;
import com.example.identity.model.Permission;
import com.example.identity.model.RefreshToken;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.BlackListRepositories;
import com.example.identity.repositories.RefreshTokenRepo;
import com.example.identity.services.JwtService;
import com.example.identity.ultis.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service

public class JwtServiceImlp implements JwtService {

    @NonFinal
    JwtConfig jwtConfig;
    @NonFinal
    Key key;
    BlackListRepositories blackListRepositories;
    RefreshTokenRepo refreshTokenRepo;

    @Autowired
    public void setKey(JwtConfig config) {
        this.jwtConfig = config;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(config.getSecretKey().getBytes()));
    }

    // Lấy username từ token
    @Override
    public String extractUserName(String token) {
        return extractClams(token, Claims::getSubject);
    }

    private <T> T extractClams(String token, Function<Claims, T> claimsMutator) {
        final Claims claims = decodeToken(token);
        return claimsMutator.apply(claims);
    }

    //Giải mã token coi có phải của mình không
    @Override
    public Claims decodeToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //Token còn thời gian hay không
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractClams(token, Claims::getExpiration);
            return expirationDate.after(new Date(System.currentTimeMillis()));
        } catch (JwtException e) {
            return false;
        }
    }

    //Token hợp lệ hay không
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    @Override
    public boolean isInssuerValid(String token) {
        return decodeToken(token).getIssuer().equals(jwtConfig.getIssuerPath());
    }

    @Override
    public boolean isBlackList(String token) {
        return blackListRepositories.existsById(token);
    }

    @Override
    public String createToken(UserAuthorRq object) {
        Date date = new Date(System.currentTimeMillis() + jwtConfig.getIssuedAt());
        return Jwts.builder().setSubject(object.getUsername()).claim("role", getRoleAndPermission(object.getRole(), object.getPermissions())).claim("id", object.getId())
                .signWith(key, SignatureAlgorithm.HS512).setIssuer(jwtConfig.getIssuerPath()).setExpiration(date).compact();
    }

    private List<String> getRoleAndPermission(Set<Role> roles, Set<Permission> permissions) {
        StringBuffer sb = new StringBuffer();
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> sb.append(role.getName()).append(" "));
        }
        if (!CollectionUtils.isEmpty(permissions)) {
            permissions.forEach(permission -> sb.append(permission.getName()).append(" "));
        }
        return Arrays.asList(sb.toString().split(" "));
    }

    @Override
    public String createRefreshToken(User user) {
        Date date = new Date(System.currentTimeMillis() + jwtConfig.getExpTokenRefresh());
        var re = Jwts.builder().setSubject(user.getUsername())
                .signWith(key, SignatureAlgorithm.HS512).setIssuer(jwtConfig.getIssuerPath()).setExpiration(date).compact();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(re);
        refreshToken.setUser(user);
        refreshToken.setExpiredDate(date);
        refreshTokenRepo.save(refreshToken);
        return re;
    }


    @Override
    public String getLastRefreshTokenFromDataBase(UUID userId) {
        var token = refreshTokenRepo.findFirstByUserIdOrderByExpiredDateDesc(userId);
        return token.filter(this::isTokenExpired).orElse(null);
    }
}
