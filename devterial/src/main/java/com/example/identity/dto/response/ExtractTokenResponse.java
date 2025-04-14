package com.example.identity.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class ExtractTokenResponse {

	private boolean isAuthen;
}
