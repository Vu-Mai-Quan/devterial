package com.example.identity.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.identity.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface JpaRepositoriyUser extends JpaRepository<User, UUID>{
	@EntityGraph(attributePaths = {"role"})
	List<User> findAll();
	@EntityGraph(attributePaths = {"role"})
	Optional<User> findByUsername(String username);

}
