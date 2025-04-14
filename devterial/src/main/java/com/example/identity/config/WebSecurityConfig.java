package com.example.identity.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.identity.repositories.JpaRepositoriyUser;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

	JpaRepositoriyUser jpaRepositoriyUser;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Bean
	UserDetailsService detailsService() {
		UserDetailsService detailsService = (username) -> jpaRepositoriyUser.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		return detailsService;
	}


	@Bean
	AuthenticationProvider authenticationProvider(UserDetailsService  detailsService) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(detailsService);
		authProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return authProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
