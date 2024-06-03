package com.backend.bms.dto;

import com.backend.bms.domain.User;
import lombok.Builder;
import lombok.Getter;

public class UserDto {
    @Builder
    @Getter
    public static class Request {
        private String name;
        private String email;

        public User toEntity() {
            return User.builder()
                    .name(name)
                    .email(email)
                    .build();
        }
    }
    @Builder
    @Getter
    public static class Response {
        private String name;
        private String email;

        public static UserDto.Response toDto(User user) {
            return Response.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }
}
