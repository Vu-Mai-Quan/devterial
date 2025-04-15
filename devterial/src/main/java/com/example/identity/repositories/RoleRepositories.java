package com.example.identity.repositories;

import com.example.identity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepositories extends JpaRepository<Role, String> {

}
