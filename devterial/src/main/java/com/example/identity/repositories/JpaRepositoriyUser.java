package com.example.identity.repositories;

import com.example.identity.model.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface JpaRepositoriyUser extends JpaRepository<User, UUID>{
	@EntityGraph(attributePaths = {"role"})
	@Override
	@NonNull
	Page<User> findAll(@NonNull  Pageable pageable);

	@Query("SELECT u From User WHERE u.userName = :username")
	@EntityGraph(attributePaths = {"role"})
	Optional<User> findByUsername(@Param("username") String username);
	

}
