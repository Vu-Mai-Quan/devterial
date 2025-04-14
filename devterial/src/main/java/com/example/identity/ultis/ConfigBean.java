package com.example.identity.ultis;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ConfigBean {

	@Bean
	ModelMapper newModelMapper() {
		return new ModelMapper();
	}

	@Bean
	BCryptPasswordEncoder endcoder() {
		BCryptPasswordEncoder end = new BCryptPasswordEncoder(10);
		return end;
	}

	
}
