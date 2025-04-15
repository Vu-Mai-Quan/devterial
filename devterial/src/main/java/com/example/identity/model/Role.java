package com.example.identity.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Role {
    @Id
    String name;
    String description;
    @ManyToMany()
    @JoinTable(name = "role_and_permisstion",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    Set<Permission> permistions;
    @ManyToMany(mappedBy = "role")
    Set<User> users;
}