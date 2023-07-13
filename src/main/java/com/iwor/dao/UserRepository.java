package com.iwor.dao;

import com.iwor.entity.User;

import javax.persistence.EntityManager;

public class UserRepository extends RepositoryBase<Long, User> {

    public UserRepository(EntityManager em) {
        super(User.class, em);
    }
    // todo
}
