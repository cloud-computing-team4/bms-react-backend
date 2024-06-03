package com.backend.bms.service;

import com.backend.bms.dto.UserDto;

import java.util.List;

public interface UserService {
    void save(UserDto.Request request);
    List<UserDto.Response> findAll();
    UserDto.Response findByName(String name);
    void deleteByName(String name);
    void update(String name, UserDto.Request request);
}
