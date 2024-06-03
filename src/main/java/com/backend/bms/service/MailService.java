package com.backend.bms.service;

import com.backend.bms.dto.MailDto;

import java.util.List;

public interface MailService {
    MailDto.Response create(MailDto.Request request);
    List<MailDto.Response> findAll();
    MailDto.Response findById(Long id);
    void deleteById(Long id);
    void update(Long id, MailDto.Request request);
    public void sendBulkMail(Long mailId);
}
