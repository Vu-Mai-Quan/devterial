package com.example.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
public class LoginResponse {
    String token;
    @JsonProperty(value = "refresh_token")
    String refreshToken;
    UserResponse user;
}
