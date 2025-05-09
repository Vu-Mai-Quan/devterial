package com.example.identity.dto.response;

import com.example.identity.model.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRp {
    String name;
    String descriptions;
    Set<Role> roles;
}
