package com.example.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
@Getter
public class GlobalResponse <T>{
	int status;
	String message;
	T result;
}
