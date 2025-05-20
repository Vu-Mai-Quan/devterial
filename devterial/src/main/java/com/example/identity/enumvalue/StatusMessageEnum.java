package com.example.identity.enumvalue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StatusMessageEnum {
    SUCCESS("Thành công", HttpStatus.OK.value()),
    SERVER_ERROR("Lỗi mạng", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    UNAUTHORIZED("Chưa được xác thực", HttpStatus.UNAUTHORIZED.value()),
    NOT_FOUND("Không tìm thấy", HttpStatus.NOT_FOUND.value()),
    BAD_REQUEST("Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST.value()),
    FORBIDDEN("Bị chặn", HttpStatus.FORBIDDEN.value());

    final String message;
    final int status;


}
