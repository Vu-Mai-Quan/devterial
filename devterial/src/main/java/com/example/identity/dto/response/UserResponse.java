package com.example.identity.dto.response;

import com.example.identity.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor

@Getter
@Setter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserResponse {

    UUID id;
    String username;
    @JsonProperty(value = "fist_name")
    String fistName;
    @JsonProperty(value = "last_name")
    String lastName;
    LocalDate dob;
    Set<Role> role;

    public UserResponse(UUID id, String username, String fistName, String lastName, LocalDate dob, Set<Role> role) {
        this.id = id;
        this.username = username;
        this.fistName = fistName;
        this.lastName = lastName;
        this.dob = dob;
        this.role = role;
    }
}
