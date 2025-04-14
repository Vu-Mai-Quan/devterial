package com.example.identity.dto.response;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.example.identity.enumvalue.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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
