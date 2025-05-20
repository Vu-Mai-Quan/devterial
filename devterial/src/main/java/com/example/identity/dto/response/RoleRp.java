package com.example.identity.dto.response;

import com.example.identity.model.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRp {
    String name;
    String descriptions;
    Set<Permission> permissions;
}
