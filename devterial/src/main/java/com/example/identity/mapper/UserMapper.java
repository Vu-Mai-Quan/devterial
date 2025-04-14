package com.example.identity.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.model.User;

@Component()
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMapper {
	ModelMapper mapper;

	public User userRequestToUser(UserRequest rq) {
		mapper.typeMap(UserRequest.class, User.class).addMappings(map-> map.skip(User::setPassword));
		User user =	rq==null?null:mapper.map(rq, User.class);
		return user;
	}

	public UserRequest userToUserRequest(User u) {
		return u==null?null:mapper.map(u, UserRequest.class);
	}

	public UserResponse userToUserResponse(User user) {
		return user==null?null:mapper.map(user, UserResponse.class);
	}
}
