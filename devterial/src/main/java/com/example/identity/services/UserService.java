package com.example.identity.services;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService extends BaseService<UserRequest, UserResponse>{

    Page<UserResponse> getAll(Pageable pageable);

}
