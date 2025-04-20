package com.example.identity.enumvalue;

public enum RoleEnum {
	ADMIN("ADMIN"), CLIENT("Khách hàng"), CUSTOMER("Nhân viên"), USER("Người dùng"),MANAGER("Người quản lí");

	RoleEnum(String string) {
		this.message = string;
	}

	String message;

	public String getMessage() {
		return message;
	}
}
