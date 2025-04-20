package com.example.identity.services;

import com.example.identity.dto.UserAuthorRq;
import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import com.example.identity.repositories.BlackListRepositories;
import com.example.identity.repositories.RoleRepositories;
import com.example.identity.ultis.JwtConfig;
import io.jsonwebtoken.Claims;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtService {

    @NonFinal
    JwtConfig jwtConfig;
    @NonFinal
    Key key;
    BlackListRepositories blackListRepositories;


    @Autowired
    public void setKey(JwtConfig config) {
        this.jwtConfig = config;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(config.getSecretKey().getBytes()));
    }

    // Lấy username từ token

    public String extractUserName(String token) {
        return extractClams(token, Claims::getSubject);
    }

    public <T> T extractClams(String token, Function<Claims, T> claimsMutator) {
        final Claims claims = decodeToken(token);
        return claimsMutator.apply(claims);
    }

    //Giải mã token coi có phải của mình không
    public Claims decodeToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //Token còn thời gian hay không
    public boolean isTokenExpired(String token) {
        Date expirationDate = extractClams(token, Claims::getExpiration);
        return expirationDate.after(new Date(System.currentTimeMillis()));
    }

    //Token hợp lệ hay không
    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    public boolean isInssuValid(String token) {
        return decodeToken(token).getIssuer().equals("vumaiquan.com");
    }

    public boolean isBlackList(String token) {
        return blackListRepositories.existsById(token);
    }

    public String createToken(UserAuthorRq object) {
        Date date = new Date(System.currentTimeMillis() + jwtConfig.getIssuedAt());
        return Jwts.builder().setSubject(object.getUsername()).claim("role", getRoleAndPermission(object.getRole(), object.getPermissions())).claim("id", object.getId())
                .signWith(key, SignatureAlgorithm.HS512).setIssuer("vumaiquan.com").setExpiration(date).compact();
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
}
