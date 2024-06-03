package com.backend.bms.controller;

import com.backend.bms.dto.MailDto;
import com.backend.bms.service.GmailService;
import com.backend.bms.service.MailService;
import com.backend.bms.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final GmailService gmailService;

    @PostMapping("/mail")
    public ResponseEntity<ApiUtils.ApiSuccess<MailDto.Response>> createPost(@RequestBody MailDto.Request request) {
        MailDto.Response response = mailService.create(request);
        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @GetMapping("/mails")
    public ResponseEntity<ApiUtils.ApiSuccess<List<MailDto.Response>>> findAllMails() {
        return ResponseEntity.ok()
                .body(ApiUtils.success(mailService.findAll()));
    }

    @GetMapping("mail/{mailId}")
    public ResponseEntity<ApiUtils.ApiSuccess<MailDto.Response>> findMail(@PathVariable Long mailId) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(mailService.findById(mailId)));
    }

    @DeleteMapping("mail/{mailId}")
    public ResponseEntity<ApiUtils.ApiSuccess<String>> deleteMail(@PathVariable long mailId) {
        mailService.deleteById(mailId);
        return ResponseEntity.ok(ApiUtils.success("메일이 삭제되었습니다."));
    }

    @PatchMapping("mail/{mailId}")
    public ResponseEntity<ApiUtils.ApiSuccess<String>> updateMail(@PathVariable Long mailId, @RequestBody MailDto.Request request) {
        mailService.update(mailId, request);
        return ResponseEntity.ok(ApiUtils.success("메일이 수정되었습니다."));
    }

    @GetMapping("/bulk-mail/{mailId}")
    public ResponseEntity<ApiUtils.ApiSuccess<String>> sendEmail(@PathVariable long mailId) {
        mailService.sendBulkMail(mailId);
        return ResponseEntity.ok(ApiUtils.success("bulk-mail이 전송되었습니다."));
    }
}
