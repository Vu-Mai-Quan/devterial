package com.example.identity.ultis;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtConfig {

    String secretKey;
    long issuedAt;

    public JwtConfig(@Value(value = "${auth.secret-key}") String secretKey, @Value(value = "${auth.exp}") long issuedAt) {
        this.secretKey = secretKey;
        this.issuedAt = issuedAt;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

}
