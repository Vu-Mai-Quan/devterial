package com.example.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "permissions")
@NamedEntityGraph(name = "Permission.roles",attributeNodes = @NamedAttributeNode("roles"))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
  @Id
  String name;
  @JsonIgnore
  String descriptions;

  @ManyToMany(mappedBy = "permissions")
  @JsonIgnore
  Set<Role> roles;
}