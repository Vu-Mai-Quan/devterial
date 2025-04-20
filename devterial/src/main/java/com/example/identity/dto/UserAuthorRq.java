package com.example.identity.dto;


import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;


@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserAuthorRq {
    UUID id;
    String username;
    Set<Role> role;
    Set<Permission> permissions;

}
