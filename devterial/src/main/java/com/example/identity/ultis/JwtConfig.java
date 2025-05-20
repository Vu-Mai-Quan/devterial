package com.example.identity.ultis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class JwtConfig {

    String secretKey, issuerPath;
    long issuedAt, expTokenRefresh;

    public JwtConfig(@Value(value = "${auth.secret-key}") String secretKey,
                     @Value(value = "${auth.exp}") long issuedAt,
                     @Value(value = "${auth.exp-refresh-token}") long expTokenRefresh,
                     @Value(value = "${auth.issuer-path}") String issuerPath
                     ) {
        this.secretKey = secretKey;
        this.issuedAt = issuedAt;
        this.expTokenRefresh = expTokenRefresh;
        this.issuerPath = issuerPath;
    }



}
