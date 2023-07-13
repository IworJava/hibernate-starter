package com.iwor.service;

import com.iwor.dao.UserRepository;
import com.iwor.dto.UserCreateDto;
import com.iwor.dto.UserReadDto;
import com.iwor.entity.User;
import com.iwor.mapper.Mapper;
import com.iwor.mapper.UserCreateMapper;
import com.iwor.mapper.UserReadMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateMapper userCreateMapper;

    public Long create(UserCreateDto dto) {
        // todo validation
        // better validate dto than entity

        var userEntity = userCreateMapper.mapFrom(dto);
        return userRepository.save(userEntity).getId();
    }

    public Optional<UserReadDto> findById(Long id) {
        return findById(id, userReadMapper);
    }

    public <T> Optional<T> findById(Long id, Mapper<User, T> mapper) {
        Map<String, Object> properties = Map.of(
                GraphSemantic.LOAD.getJpaHintName(),
                userRepository.getEntityManager()
                        .getEntityGraph("withCompany")
        );
        return userRepository.findById(id, properties)
                .map(mapper::mapFrom);
    }

    public boolean delete(Long id) {
        var maybeUser = userRepository.findById(id);
        maybeUser.ifPresent(user -> userRepository.delete(user.getId()));
        return maybeUser.isPresent();
    }
}
