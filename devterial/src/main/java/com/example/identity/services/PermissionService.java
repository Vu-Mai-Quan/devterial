package com.example.identity.services;

import com.example.identity.dto.request.PermissionRq;
import com.example.identity.dto.response.PermissionRp;
import com.example.identity.model.Role;

import java.util.UUID;

public interface PermissionService extends BaseService<PermissionRq, PermissionRp>{

    void delete(String name);

    /**
     * Tìm một Permission từ Id của nó;
     * */
    PermissionRp getOne(String id);


    PermissionRp update(PermissionRq rq, Role role);


}
