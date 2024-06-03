package com.backend.bms.controller;

import com.backend.bms.dto.UserDto;
import com.backend.bms.service.UserService;
import com.backend.bms.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody UserDto.Request request) {
        userService.save(request);
        return ResponseEntity.ok(ApiUtils.success("유저가 생성되었습니다."));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiUtils.ApiSuccess<List<UserDto.Response>>> findAllUser() {
        return ResponseEntity.ok()
                .body(ApiUtils.success(userService.findAll()));
    }

    @GetMapping("user/{name}")
    public ResponseEntity<ApiUtils.ApiSuccess<UserDto.Response>> findByName(@PathVariable String name) {
        return ResponseEntity.ok(ApiUtils.success(userService.findByName(name)));
    }

    @DeleteMapping("user/{name}")
    public ResponseEntity<ApiUtils.ApiSuccess<String>> deleteUser(@PathVariable String name) {
        userService.deleteByName(name);
        return ResponseEntity.ok(ApiUtils.success("유저가 삭제되었습니다."));
    }

    @PatchMapping("user/{name}")
    public ResponseEntity<ApiUtils.ApiSuccess<String>> updateUser(@PathVariable String name, @RequestBody UserDto.Request request) {
        userService.update(name, request);
        return ResponseEntity.ok(ApiUtils.success("유저의 이메일이 변경되었습니다."));
    }

}
