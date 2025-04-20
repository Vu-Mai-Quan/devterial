package com.example.identity.dto.request;

import com.example.identity.enumvalue.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRq {
    @JsonProperty(value = "role_enum")
    RoleEnum roleEnum;
    Set<String> permissions;
}
