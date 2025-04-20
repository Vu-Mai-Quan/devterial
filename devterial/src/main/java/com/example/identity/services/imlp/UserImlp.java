package com.example.identity.services.imlp;

import com.example.identity.dto.UserAuthorRq;
import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.mapper.UserMapper;
import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.repositories.RoleRepositories;
import com.example.identity.services.AuthService;
import com.example.identity.services.BaseService;
import com.example.identity.services.JwtService;
import com.example.identity.ultis.AuthorityUltis;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.AuthenticationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserImlp implements BaseService<UserRequest, UserResponse>, AuthService {

    JpaRepositoriyUser repo;
    UserMapper uMap;
    PasswordEncoder endCode;
    AuthorityUltis authorityUltis;
    JwtService jwtService;
    RoleRepositories roleRepositories;


    @Override
    @Transactional
    public UserResponse save(UserRequest data) {
        User user = uMap.userRequestToUser(data);
        user.setPassword(endCode.encode(data.getPassword()));
        repo.save(user);
        return uMap.userToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(uMap::userToUserResponse).toList();
    }

    @Override
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getOne(UUID id) {
        User user = repo.findById(id).orElseThrow(() -> new NoResultException("Không có user với id: " + id));
        return uMap.userToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UserRequest rq) throws AuthenticationException {
        User targetUser = repo.findById(id)
                .orElseThrow(() -> new EntityExistsException("Không tồn tại user với id: " + id));

        User updatedUser = uMap.userRequestToUser(rq);
        log.info(updatedUser.getPassword());
        Set<String> currentRoles = authorityUltis.getAllRoleTagertUser(null);
        Set<String> targetRoles = authorityUltis.getAllRoleTagertUser(targetUser);

        boolean isSelf = authorityUltis.isCurrentNameLogin(targetUser.getUsername());
        boolean isAdmin = currentRoles.contains("ROLE_ADMIN");
        boolean isCustomer = currentRoles.contains("ROLE_CUSTOMER");
        boolean targetIsAdmin = targetRoles.contains("ROLE_ADMIN");
        boolean targetIsCustomer = targetRoles.contains("ROLE_CUSTOMER");
        boolean targetIsClient = targetRoles.contains("ROLE_CLIENT");

        // Phân quyền theo yêu cầu
        if (isAdmin) {
            // Admin được cập nhật tất cả, bao gồm username
            updatedUser.setId(id);
            updatedUser.setPassword(endCode.encode(rq.getPassword()));
            repo.save(updatedUser);
            return uMap.userToUserResponse(updatedUser);
        }

        if (isCustomer) {
            // CUSTOMER chỉ được cập nhật người có quyền CLIENT
            if (targetIsClient && !targetIsAdmin && !targetIsCustomer) {
                updatedUser.setId(id);
                updatedUser.setUsername(targetUser.getUsername());
                updatedUser.setPassword(endCode.encode(rq.getPassword()));// Không cho sửa username
                updatedUser.setRole(targetUser.getRole());
                repo.save(updatedUser);
                return uMap.userToUserResponse(updatedUser);
            }
        }

        if (isSelf && (currentRoles.contains("ROLE_CLIENT"))) {
            updatedUser.setId(id);
            updatedUser.setUsername(targetUser.getUsername());
            updatedUser.setPassword(endCode.encode(rq.getPassword()));// Không cho sửa username
            updatedUser.setRole(targetUser.getRole());
            repo.save(updatedUser);
            return uMap.userToUserResponse(updatedUser);
        }

        throw new AuthenticationException("Không đủ quyền cập nhật");

    }

    @Override
    public LoginResponse login(LoginRequest rq) {
        Optional<User> user = repo.findByUsername(rq.getUsername());
        if (user.isPresent() && endCode.matches(rq.getPassword(), user.get().getPassword())) {
            Set<Permission> per = getAllPermissionByIdRole(user.get().getRole());
            return LoginResponse.builder()
                    .token(jwtService.createToken(new UserAuthorRq(user.get().getId(), user.get().getUsername(), user.get().getRole(), per)))
                    .build();
        } else {
            throw new NoResultException("Thông tin tài khoản hoặc mật khẩu không chính xác");
        }

    }

    private Set<Permission> getAllPermissionByIdRole(Set<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }
        Set<String> id = roles.stream().map(Role::getName).collect(Collectors.toSet());
        return roleRepositories.findAllPermissionByIdRole(id).stream().flatMap(role -> role.getPermissions().stream()).collect(Collectors.toSet());
    }


    @Override
    public boolean isAuthencate(String token) {
        return false;
    }

}
