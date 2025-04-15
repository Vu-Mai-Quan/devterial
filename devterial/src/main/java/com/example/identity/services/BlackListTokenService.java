package com.example.identity.services;

import java.util.Date;

public interface BlackListTokenService {
   void createBlackListToken(String token, Date expiredDate);
}
