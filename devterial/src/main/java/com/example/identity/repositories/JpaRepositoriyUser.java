package com.example.identity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.identity.model.User;

import java.util.Optional;
import java.util.UUID;


public interface JpaRepositoriyUser extends JpaRepository<User, UUID>{

	Optional<User> findByUsername(String username);

}
