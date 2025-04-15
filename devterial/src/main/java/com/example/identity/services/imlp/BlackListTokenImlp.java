package com.example.identity.services.imlp;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.identity.model.BlackListToken;
import com.example.identity.repositories.BlackListRepositories;
import com.example.identity.services.BlackListTokenService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlackListTokenImlp implements BlackListTokenService {
	Logger log = LoggerFactory.getLogger(BlackListTokenImlp.class);
	
	BlackListRepositories blackListRepositories;
    /**
     * Tạo ra một token mới
     */
    @Override
	public void createBlackListToken(String token, Date expiredDate) {
		// Lưu token vào cơ sở dữ liệu hoặc bộ nhớ cache với thời gian hết hạn
		// Ví dụ: sử dụng Redis hoặc cơ sở dữ liệu để lưu trữ token
		// Bạn có thể sử dụng một thư viện như RedisTemplate hoặc JPA để thực hiện điều
		// này
    	Optional<BlackListToken> blackListToken = blackListRepositories.findByToken(token);	
    	if (blackListToken.isPresent()) {
    		throw new RuntimeException("Token đã tồn tại trong danh sách đen"); 
    	} else {
    		BlackListToken blackList = new BlackListToken();
    		blackList.setToken(token);
    		blackList.setExpiredDate(expiredDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    		blackListRepositories.save(blackList);
    	}
        log.warn("Token đã bị đen danh sách: {}", token);
        
    }
}

