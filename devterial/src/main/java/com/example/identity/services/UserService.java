package com.example.identity.services;

import java.util.List;
import java.util.UUID;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.model.User;

public interface UserService {
	User save (UserRequest data); 
	List<UserRequest> getAll();
	UserRequest getOne(UUID id);
	User update(UUID id,UserRequest rq);
}
