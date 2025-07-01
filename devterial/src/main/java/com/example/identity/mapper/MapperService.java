package com.example.identity.mapper;

import com.example.identity.model.Permission;
import org.modelmapper.ModelMapper;

public abstract class MapperService {
    protected final ModelMapper mapper;

    protected MapperService(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public abstract <R, RQ> R requestToModel(RQ rq);

    public abstract <R, RQ> R modelToResponse(RQ rq);

    public abstract <R, RQ> R requestToResponse(RQ rq);
}
