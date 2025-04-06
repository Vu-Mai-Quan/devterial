package com.example.identity.ultis;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBean {

	@Bean
	ModelMapper newModelMapper() {
		return new ModelMapper();
	}

}
