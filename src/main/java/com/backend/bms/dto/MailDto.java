package com.backend.bms.dto;

import com.backend.bms.domain.Mail;
import lombok.Builder;
import lombok.Getter;

public class MailDto {
    @Builder
    @Getter
    public static class Request{
        private String title;
        private String content;

        public Mail toEntity() {
            return Mail.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String content;

        public static MailDto.Response toDto(Mail mail) {
            return Response.builder()
                    .id(mail.getId())
                    .title(mail.getTitle())
                    .content(mail.getContent())
                    .build();
        }
    }
}
