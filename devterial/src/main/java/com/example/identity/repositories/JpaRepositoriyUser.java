package com.example.identity.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.identity.model.User;

public interface JpaRepositoriyUser extends JpaRepository<User, UUID>{

}
