package com.example.identity.dto.request;


import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;


@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserAuthorRq {
    UUID id;
    String username;
    Set<Role> role;
    Set<Permission> permissions;



}
