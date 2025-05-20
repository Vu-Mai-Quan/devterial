package com.example.identity.services.imlp;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.mapper.UserMapper;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.services.UserService;
import com.example.identity.ultis.AuthorityUltis;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Set;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserImlp implements UserService {

    JpaRepositoriyUser repo;
    UserMapper uMap;
    PasswordEncoder endCode;
    AuthorityUltis authorityUltis;


    @Override
    @Transactional
    public UserResponse save(UserRequest data) {
        User user = uMap.userRequestToUser(data);
        user.setPassword(endCode.encode(data.getPassword()));
        user.setRole(Set.of(Role.builder().name(RoleEnum.USER.name()).build(), Role.builder().name(RoleEnum.CLIENT.name()).build()));
        repo.save(user);
        return uMap.userToUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(uMap::userToUserResponse);
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

//        if (isCustomer) {
//            // CUSTOMER chỉ được cập nhật người có quyền CLIENT
//            if (targetIsClient && !targetIsAdmin && !targetIsCustomer) {
//                rq.setId(id);
//        rq.setUsername(targetUser.getUsername());
//        rq.setPassword(endCode.encode(rq.getPassword()));// Không cho sửa username
//        rq.setRole(targetUser.getRole());
//        repo.save(rq);
//            }
//        }

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

    private User updateWithUser(UserRequest rq) {
        return null;
    }



    //    private User updateWithCustomer(User rq){
//
//        return rq;
//    };
    private User updateWithAdmin(UserRequest rq) {
        return null;
    }




}
