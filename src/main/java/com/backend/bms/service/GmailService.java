package com.backend.bms.service;

public interface GmailService {
    void sendEmail(String to, String subject, String text);
    void sendEmail(String[] to, String subject, String text);
    String setContext(String dynamicHtml);
}
