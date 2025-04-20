package com.example.identity.services;

import com.example.identity.dto.response.GlobalResponse;

import java.util.Date;

public interface BlackListTokenService {
   GlobalResponse<?> createBlackListToken(String token);
}
