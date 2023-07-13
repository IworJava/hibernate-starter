package com.iwor.mapper;

public interface Mapper<F, T> {
    T mapFrom(F object);
}
