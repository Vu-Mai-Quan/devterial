package com.example.identity.mapper;

import com.example.identity.dto.request.RoleRq;
import com.example.identity.dto.response.RoleRp;
import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.model.Role;
import com.example.identity.repositories.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleMapper {
    ModelMapper modelMapper;

    public Role roleRqToRole(RoleRq rq) {
        Converter<RoleEnum, String> roleEnumToName = ctx -> ctx.getSource() != null ? ctx.getSource().name() : null;
        Converter<RoleEnum, String> roleEnumToDesc = ctx -> ctx.getSource() != null ? ctx.getSource().getMessage() : null;

        modelMapper.typeMap(RoleRq.class, Role.class).addMappings(mp -> {
            mp.using(roleEnumToName).map(RoleRq::getRoleEnum,Role::setName);
            mp.using(roleEnumToDesc).map(RoleRq::getRoleEnum,Role::setDescriptions);
            mp.skip(Role::setPermissions);
            mp.skip(Role::setUsers);
        });
        return rq==null?null:modelMapper.map(rq, Role.class);
    }

    public RoleRp roleToRoleRp(Role rq) {
        if(rq==null){
            return null;
        }
        var data = modelMapper.map(rq, RoleRp.class);
        data.setPermissions(rq.getPermissions());
        return data;
    }


}
