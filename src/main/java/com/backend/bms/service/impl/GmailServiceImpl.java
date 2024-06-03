package com.backend.bms.service.impl;

import com.backend.bms.repository.MailRepository;
import com.backend.bms.repository.UserRepository;
import com.backend.bms.service.GmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class GmailServiceImpl implements GmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private final UserRepository userRepository;
    private final MailRepository mailRepository;

    public void sendEmail(String to, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(setContext(text), true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendEmail(String[] to, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(setContext(text), true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String setContext(String dynamicHtml) {
        Context context = new Context();
        context.setVariable("dynamicContent", dynamicHtml);
        return templateEngine.process("mail", context);
    }
}
