package com.example.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "ROLE",
        attributeNodes = @NamedAttributeNode("permissions")
)
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NamedQuery(name="Role.selectIn", query = "select r FROM Role r WHERE r.name IN :id")
public class Role {
    @Id 
    String name;
    @JsonIgnore
    String descriptions;
    @ManyToMany()
    @JoinTable(name = "role_and_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @JsonIgnore
    Set<Permission> permissions;
    @ManyToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<User> users;
}