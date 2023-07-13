package com.iwor.dao;

import com.iwor.entity.Company;

import javax.persistence.EntityManager;

public class CompanyRepository extends RepositoryBase<Integer, Company> {

    public CompanyRepository(EntityManager em) {
        super(Company.class, em);
    }
}
