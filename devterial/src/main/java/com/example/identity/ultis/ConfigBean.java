package com.example.identity.ultis;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ConfigBean {

	@Bean
	ModelMapper newModelMapper() {
		return new ModelMapper();
	}

	@Bean
	PasswordEncoder endcoder() {
        return new BCryptPasswordEncoder(10);
	}


}
