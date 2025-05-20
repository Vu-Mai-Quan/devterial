package com.example.identity.dto.response;

import com.example.identity.enumvalue.StatusMessageEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

import java.util.Date;

@JsonInclude(value = Include.NON_NULL)
@Getter
public class GlobalResponse<T> {
    int code;
    String message, status;
    T result;

    final Date timestamp = new Date(System.currentTimeMillis());

    public GlobalResponse(StatusMessageEnum enumMessage, String message, T result) {
        this.status = enumMessage.name();
        this.code = enumMessage.getStatus();
        this.message = message;
        this.result = result;
    }


}
