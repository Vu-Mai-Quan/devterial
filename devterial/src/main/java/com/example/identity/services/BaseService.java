package com.example.identity.services;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.UUID;

public interface BaseService<I, O> {
    default O save(I data) {
        return null;
    }

    default List<O> getAll() {
        return List.of();
    }

    default O getOne(UUID id) {
        return null;
    }

    default O update(UUID id, I rq) throws AuthenticationException {
        return null;
    }


}
