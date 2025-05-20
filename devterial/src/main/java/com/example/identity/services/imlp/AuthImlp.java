package com.example.identity.services.imlp;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.mapper.UserMapper;
import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.repositories.RoleRepositories;
import com.example.identity.services.AuthService;
import com.example.identity.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthImlp implements AuthService {

    JpaRepositoriyUser repo;
    PasswordEncoder endCode;
    JwtService jwtService;
    RoleRepositories roleRepositories;
    UserMapper mapper;

    @Override
    @Transactional
    public LoginResponse login(@NonNull LoginRequest rq) {
        Optional<User> user = repo.findByUsername(rq.getUsername());
        if (user.isPresent() && endCode.matches(rq.getPassword(), user.get().getPassword())) {
            Set<Permission> per = getAllPermissionByIdRole(user.get().getRole());
            String token = jwtService.getLastRefreshTokenFromDataBase(user.get().getId());
            return LoginResponse.builder()
                    .token(jwtService.createToken(mapper.userAuthorRqToUser(user.get(), per)))
                    .user(new UserResponse(
                            user.get().getId(), user.get().getUsername(), user.get().getFistName(), user.get().getLastName(), null, null
                    )).refreshToken(token == null ? jwtService.createRefreshToken(user.get()) : token)
                    .build();
        } else {
            throw new NoResultException("Thông tin tài khoản hoặc mật khẩu không chính xác");
        }

    }

    @Override
    @Transactional
    public String createAccessTokenByRefreshToken(String rftoken) {
        try {
            Optional<User> user = repo.findByUsername(jwtService.decodeToken(rftoken).getSubject());
            if (user.isPresent()) {
                Set<Permission> per = getAllPermissionByIdRole(user.get().getRole());
                return jwtService.createToken(mapper.userAuthorRqToUser(user.get(), per));
            }
            throw new EntityNotFoundException("Không tìm thấy user !");
        } catch (ExpiredJwtException e) {
            throw new JwtException("Refresh token đã hết hạn !", e);
        }
    }


    private Set<Permission> getAllPermissionByIdRole(Set<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.collectingAndThen(
                        Collectors.toSet(),
                        roleNames -> roleRepositories.findAllPermissionByIdRole(roleNames)
                                .stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .collect(Collectors.toSet())
                ));
    }

    @Override
    @Transactional
    public void decentralizationUser(UUID id,@NonNull List<RoleEnum> decentralizationRq) {
        var user = repo.findById(id).orElseThrow(() -> new NoResultException("Không có user với id: " + id));
        Set<Role> roles = roleRepositories.findAllById(decentralizationRq.stream().map(RoleEnum::name).collect(Collectors.toSet()));
        user.setRole(roles);
        repo.save(user);
    }



}
