package com.example.identity.services;

import com.example.identity.dto.request.PermissionRq;
import com.example.identity.dto.response.PermissionRp;

public interface PermissionService extends BaseService<PermissionRq, PermissionRp>{

    void delete(String name);
}
