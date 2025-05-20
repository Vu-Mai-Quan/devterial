package com.example.identity.enumvalue;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN("ADMIN"), CLIENT("Khách hàng"), CUSTOMER("Nhân viên"), USER("Người dùng"), MANAGER("Người quản lí");

    final String message;


}
