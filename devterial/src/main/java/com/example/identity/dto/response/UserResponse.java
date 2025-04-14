package com.example.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.identity.enumvalue.RoleEnum;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
	UUID id;
	String username;
	@JsonProperty(value  = "fist_name")
	String fistName;
	@JsonProperty(value = "last_name")
	String lastName;
	LocalDate dob;
	Set<RoleEnum> role;
}
