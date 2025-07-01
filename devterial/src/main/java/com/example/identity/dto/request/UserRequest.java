package com.example.identity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;



@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class UserRequest {


	@Email(message = "Vui lòng nhập đúng định dạng email")
	String username;
	@Size(min = 6, max = 10,message = "Mật khẩu từ 6-10 kí tự")
	String password;
	@JsonProperty(value  = "fist_name")
	String fistName;
	@JsonProperty(value = "last_name")
	String lastName;
	@JsonProperty(value = "date_of_birth")
	LocalDate dob;

	public UserRequest(String password, String fistName, String lastName, LocalDate dob) {
		this.password = password;
		this.fistName = fistName;
		this.lastName = lastName;
		this.dob = dob;
	}
}

