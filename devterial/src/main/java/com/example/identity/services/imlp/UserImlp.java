package com.example.identity.services.imlp;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.mapper.UserMapper;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
@Slf4j
public class UserImlp implements UserService {

    JpaRepositoriyUser repo;
    UserMapper uMap;
    PasswordEncoder endCode;


    public UserImlp(final JpaRepositoriyUser repo, final UserMapper uMap, final PasswordEncoder endCode) {
        this.repo = repo;
        this.uMap = uMap;
        this.endCode = endCode;
    }

    @Override
    @Transactional
    public UserResponse save(UserRequest data) {
        try {
            User user = uMap.userRequestToUser(data);
            user.setPassword(endCode.encode(data.getPassword()));
            user.setRole(Set.of(Role.builder().name(RoleEnum.USER.name()).build(), Role.builder().name(RoleEnum.CLIENT.name()).build()));
            repo.save(user);
            return uMap.userToUserResponse(user);
        } catch (EntityExistsException e) {
            throw new EntityExistsException(e.getMessage());
        }
    }

    @Override
    public Page<UserResponse> getAll(Pageable pageable) {
        return repo.findAllWithRole(pageable);
    }

    @Override
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getOne(UUID id) {
        User user = repo.findById(id).orElseThrow(() -> new NoResultException("Không có user với id: " + id));
        return uMap.userToUserResponse(user);
    }

    @Override
    @Transactional
    @PostAuthorize("rq.username == authentication.name || authentication.getAuthorities().contains('ROLE_ADMIN')||authentication.getAuthorities().contains('ROLE_MANAGER')")
    public UserResponse update(UUID id, UserRequest rq) throws AuthenticationException {
        return null;

    }

    private User updateWithUser(UserRequest rq, User user) {
        return null;
    }


    private User updateWithCustomer(UserRequest rq, User user) {
        var u = uMap.userRequestToUser(rq);
        u.setPassword(endCode.encode(rq.getPassword()));
        ModelMapper mapp = new ModelMapper();
        mapp.typeMap(UserRequest.class, User.class).addMappings(s->{
           s.skip(User::setPassword);
           s.skip(User::setUsername);
           s.skip(User::setRole);
        });
        mapp.map(u, user);
        return u;
    }

    ;

    private User updateWithAdmin(UserRequest rq, User user) {
        return null;
    }


}
