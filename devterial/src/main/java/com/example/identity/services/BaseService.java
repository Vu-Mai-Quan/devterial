package com.example.identity.services;

import java.util.List;
import java.util.UUID;

import javax.naming.AuthenticationException;

public interface BaseService<I,O> {
	O save (I data); 
	List<O> getAll();
	O getOne(UUID id);
	O update(UUID id,I rq) throws AuthenticationException;
}
