package com.example.identity.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.identity.model.BlackListToken;

public interface BlackListRepositories extends JpaRepository<BlackListToken, String> {
	Optional<BlackListToken> findByToken(String token);

	Optional<BlackListToken> findByUserId(UUID userId);
	Optional<BlackListToken> findByUserIdAndToken(UUID userId, String token);
}
