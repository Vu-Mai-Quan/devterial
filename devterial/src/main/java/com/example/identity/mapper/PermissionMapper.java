package com.example.identity.mapper;

import com.example.identity.dto.request.PermissionRq;
import com.example.identity.dto.response.PermissionRp;
import com.example.identity.model.Permission;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionMapper {
   private final ModelMapper mapper;

    public Permission toPermission (PermissionRq permissionRq){
      return permissionRq!=null?mapper.map(permissionRq, Permission.class):null;
   }
    public PermissionRp toPermissionRp (Permission permission){
        return permission!=null?mapper.map(permission, PermissionRp.class):null;
    }


}
