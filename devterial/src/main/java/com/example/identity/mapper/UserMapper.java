package com.example.identity.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.model.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component()
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMapper {
	ModelMapper mapper;
	
	public User userRequestToUser(UserRequest rq) {
		mapper.typeMap(UserRequest.class, User.class).addMappings(map-> map.skip(User::setPassword));
		User user =	mapper.map(rq, User.class);
		return user;
	}
	
	public UserRequest userToUserRequest(User u) {
		return mapper.map(u, UserRequest.class);
	}
	
}
