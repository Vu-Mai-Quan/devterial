package com.example.identity.repositories;

import com.example.identity.model.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlackListRepositories extends JpaRepository<BlackListToken, String> {

    Optional<BlackListToken> findByToken(String token);

    Optional<BlackListToken> findByUserId(UUID userId);

    Optional<BlackListToken> findByUserIdAndToken(UUID userId, String token);

}
