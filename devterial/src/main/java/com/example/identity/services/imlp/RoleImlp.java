package com.example.identity.services.imlp;

import com.example.identity.dto.request.RoleRq;
import com.example.identity.dto.response.RoleRp;
import com.example.identity.mapper.RoleMapper;
import com.example.identity.repositories.PermissionRepository;
import com.example.identity.repositories.RoleRepositories;
import com.example.identity.services.RoleService;
import jakarta.persistence.NoResultException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoleImlp implements RoleService {
    RoleRepositories roleRepositories;
    PermissionRepository permissionRepository;
    RoleMapper mapper;

    @Override
    public List<RoleRp> getAll() {
        var data = roleRepositories.findAll();
        return data.stream().map(mapper::roleToRoleRp).toList();
    }

    @Override
    public RoleRp getOne(String id) {
        var role = roleRepositories.findById(id).orElseThrow(() -> new NoResultException("Không có Role này"));
        return mapper.roleToRoleRp(role);
    }

    @Override
    public RoleRp update(String id, RoleRq rq) {
        if (!roleRepositories.existsById(id)) {
            throw new NoResultException("Không có Role này");
        }
        var role = mapper.roleRqToRole(rq);
        role.setName(id);
        role.setPermissions(new HashSet<>(permissionRepository.findAllById(rq.getPermissions())));
        roleRepositories.save(role);
        return mapper.roleToRoleRp(role);
    }
}
