package com.iwor.dao;

import com.iwor.entity.Payment;

import javax.persistence.EntityManager;

public class PaymentRepository extends RepositoryBase<Long, Payment> {

    public PaymentRepository(EntityManager em) {
        super(Payment.class, em);
    }
}
