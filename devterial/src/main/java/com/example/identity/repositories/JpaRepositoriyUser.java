package com.example.identity.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.identity.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface JpaRepositoriyUser extends JpaRepository<User, UUID>{
	@EntityGraph(attributePaths = {"role"})
	List<User> findAll();

	@Query("SELECT u FROM User u WHERE u.username = :username")
	@EntityGraph(attributePaths = {"role"})
	Optional<User> findByUsername(@Param("username") String username);

}
