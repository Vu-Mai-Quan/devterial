package com.example.identity.mapper;

import com.example.identity.dto.request.UserAuthorRq;
import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.model.Permission;
import com.example.identity.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("userMapper")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMapper {
    ModelMapper mapper;

    public User userRequestToUser(UserRequest rq) {
        mapper.typeMap(UserRequest.class, User.class).addMappings(map -> {
            map.skip(User::setPassword);
            map.skip(User::setRole);
        });
        return rq == null ? null : mapper.map(rq, User.class);
    }

    public UserAuthorRq userAuthorRqToUser(User u, Set<Permission> permissions) {
        if (u == null) {
            return null;
        }
        mapper.typeMap(User.class, UserAuthorRq.class).addMappings(map -> {
            map.map(User::getUsername, UserAuthorRq::setUsername);
            map.map(User::getId, UserAuthorRq::setId);
            map.map(User::getRole, UserAuthorRq::setRole);
            map.skip(UserAuthorRq::setPermissions);
        });
        var userRq = mapper.map(u, UserAuthorRq.class);
        userRq.setPermissions(permissions);
        return userRq;
    }

    public UserResponse userToUserResponse(User user) {
        return user == null ? null : mapper.map(user, UserResponse.class);
    }
}
