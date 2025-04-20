package com.example.identity.repositories;

import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepositories extends JpaRepository<Role, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    @Override
    @EntityGraph(attributePaths = {"permissions"})
    List<Role> findAll();

    @Override
    @EntityGraph(attributePaths = {"permissions"})
    Optional<Role> findById(String s);
    @Query("SELECT r FROM Role r WHERE r.name IN :id")
    Set<Role> findAllById(@PathParam("id") Set<String> id);
    @EntityGraph(attributePaths = {"permissions"})
    @Query("SELECT r FROM Role r WHERE r.name IN :id")
    Set<Role> findAllPermissionByIdRole(@PathParam("id") Set<String> id);
}
