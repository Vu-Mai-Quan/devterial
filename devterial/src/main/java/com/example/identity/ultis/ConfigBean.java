package com.example.identity.ultis;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ConfigBean {

	@Bean
	@Scope(value = "singleton")
	ModelMapper newModelMapper() {
		return new ModelMapper();
	}

	@Bean
	@ConditionalOnMissingBean(BCryptPasswordEncoder.class)
	@Scope(value = "singleton")
	PasswordEncoder endcoder() {
        return new BCryptPasswordEncoder(10);
	}


}
