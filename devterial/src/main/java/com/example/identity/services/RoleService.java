package com.example.identity.services;

import com.example.identity.dto.request.RoleRq;
import com.example.identity.dto.response.RoleRp;

public interface RoleService extends BaseService<RoleRq, RoleRp>{
    RoleRp getOne(String id);
    RoleRp update(String id, RoleRq rq);

}
