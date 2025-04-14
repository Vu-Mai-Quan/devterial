package com.example.identity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.identity.ultis.JwtFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthConfig {

	private String[] postPublic = { "/auth/login", "/user/register" }, getPublic = {"/product/", "/product/**" },
			getPrivave = { "/user", "/user/get-user/**" };

	private final JwtFilter config;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity rq) throws Exception {
		rq.formLogin(AbstractHttpConfigurer::disable)
		.httpBasic(AbstractHttpConfigurer::disable)
		.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, postPublic).permitAll()
						.requestMatchers(HttpMethod.PUT, "/user/**").permitAll()
						.requestMatchers(HttpMethod.GET, getPrivave).hasAnyRole("CUSTOMER", "ADMIN")
						.requestMatchers(HttpMethod.GET, getPublic).permitAll().anyRequest().authenticated())
				.sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(config, UsernamePasswordAuthenticationFilter.class)
				.cors(AbstractHttpConfigurer::disable);

		return rq.build();
	}

}
