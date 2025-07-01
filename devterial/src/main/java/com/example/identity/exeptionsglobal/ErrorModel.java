package com.example.identity.exeptionsglobal;

import com.example.identity.enumvalue.StatusMessageEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorModel {
    @JsonProperty(value = "error_code")
    StatusMessageEnum errorCode;
    @JsonProperty(value = "error_message")
    String errorMessage;
    @Setter(AccessLevel.NONE)

    Date time = new Date(System.currentTimeMillis());
    @JsonProperty(value = "error_at_url")
    String errorAt;

    public ErrorModel(
            StatusMessageEnum errorCode,
            String errorMessage,
            String errorAt
    ) {
        this.errorAt = errorAt;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

}
