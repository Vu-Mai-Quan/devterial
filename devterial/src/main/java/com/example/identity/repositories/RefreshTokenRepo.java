package com.example.identity.repositories;

import com.example.identity.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, String> {
    //    @Query(value = "Select rf.refreshToken from RefreshToken rf where rf.user.id = :id_user order by rf.expiredDate desc limit 1")
    @Query(value = "Select rf.refresh_token from refresh_tokens rf where rf.user_id = :id_user order by rf.expired_date desc limit 1", nativeQuery = true)
    Optional<String> findFirstByUserIdOrderByExpiredDateDesc(@Param("id_user") UUID id);
}
