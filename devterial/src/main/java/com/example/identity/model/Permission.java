package com.example.identity.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "permistion")
@NamedEntityGraph(name = "Permission.roles",attributeNodes = @NamedAttributeNode("roles"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission {
  @Id
  String name;
  String description;

  @ManyToMany(mappedBy = "permistions")
  Set<Role> roles;
}