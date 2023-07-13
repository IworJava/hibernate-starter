package com.iwor.dao;

import com.iwor.entity.BaseEntity;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class RepositoryBase<K extends Serializable, E extends BaseEntity<K>> implements Repository<K, E> {

    private final Class<E> clazz;
    private final EntityManager em;

    @Override
    public E save(E entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public void delete(K id) {
        em.remove(em.find(clazz, id));
        em.flush();
    }

    @Override
    public void update(E entity) {
        em.merge(entity);
    }

    @Override
    public Optional<E> findById(K id) {
        return Optional.ofNullable(em.find(clazz, id));
    }

    @Override
    public List<E> findAll() {
        var criteria = em.getCriteriaBuilder().createQuery(clazz);
        criteria.from(clazz);
        return em.createQuery(criteria)
                .getResultList();
    }
}
