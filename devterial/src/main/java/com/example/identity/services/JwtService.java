package com.example.identity.services;

import com.example.identity.ultis.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {

	@NonFinal
	JwtConfig config;
	@NonFinal
	Key key;


	@Autowired
	public void setKey(JwtConfig config) {
		this.config = config;
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
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			return claims;
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
}
