package com.backend.bms.service.impl;

import com.backend.bms.domain.User;
import com.backend.bms.dto.UserDto;
import com.backend.bms.exception.DuplicateEmailException;
import com.backend.bms.exception.DuplicateNameException;
import com.backend.bms.repository.UserRepository;
import com.backend.bms.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public void save(UserDto.Request request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 유저 이메일입니다: " + request.getEmail());
        }
        if (userRepository.existsByName(request.getName())) {
            throw new DuplicateNameException("이미 존재하는 유저 이름입니다: " + request.getName());
        }
        userRepository.save(request.toEntity());
    }

    public List<UserDto.Response> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserDto.Response::toDto)
                .toList();
    }

    public UserDto.Response findByName(String name){
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다: " + name));

        return UserDto.Response.toDto(user);
    }

    @Transactional
    public void deleteByName(String name){
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다: " + name));
        userRepository.delete(user);
    }

    @Transactional
    public void update(String name, UserDto.Request request){
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다: " + name));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 유저 이메일입니다: " + request.getEmail());
        }
        user.updateEmail(request.getEmail());
    }
}
