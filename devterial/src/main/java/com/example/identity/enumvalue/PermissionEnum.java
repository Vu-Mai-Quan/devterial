package com.example.identity.enumvalue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PermissionEnum {
    APPROVE_POST("Người dùng có quyền post"),
    CREATE_DATA("Người dùng có quyền tạo dữ liệu"),
    UPDATE_DATA("Người dùng có quyền cập nhật"),
    DELETE_DATA("Người dùng có quyền xóa dữ liệu"),
    READ_DATA("Người dùng được phép xem dữ liệu")
    ;
   private final String descriptions;
}
