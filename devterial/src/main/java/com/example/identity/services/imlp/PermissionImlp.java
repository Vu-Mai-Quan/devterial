package com.example.identity.services.imlp;

import com.example.identity.dto.request.PermissionRq;
import com.example.identity.dto.response.PermissionRp;
import com.example.identity.mapper.PermissionMapper;
import com.example.identity.model.Role;
import com.example.identity.repositories.PermissionRepository;
import com.example.identity.services.PermissionService;
import jakarta.persistence.NoResultException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PermissionImlp implements PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper mapper;

    @Override
    public PermissionRp save(PermissionRq data) {
        var permission = mapper.toPermission(data);
        permissionRepository.save(permission);
        return mapper.toPermissionRp(permission);
    }

    @Override
    public List<PermissionRp> getAll() {
        return permissionRepository.findAll().stream().map(mapper::toPermissionRp).toList();
    }

    @Override
    public PermissionRp getOne(String id) {
        return permissionRepository.findById(id).map(mapper::toPermissionRp).orElseThrow(() -> new NoResultException("Không có permission này"));
    }

    @Override
    public PermissionRp update(PermissionRq rq, Role role) {

        return null;
    }

    @Override
    public PermissionRp update(UUID id, PermissionRq rq) throws AuthenticationException {
        return null;
    }


    public void delete(String name) {
        permissionRepository.deleteById(name);
    }

    ;
}
