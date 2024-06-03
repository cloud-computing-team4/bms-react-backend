package com.backend.bms.service.impl;

import com.backend.bms.domain.Mail;
import com.backend.bms.dto.MailDto;
import com.backend.bms.repository.MailRepository;
import com.backend.bms.service.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor // 생성자 주입
@Service
@Transactional(readOnly = true)
public class MailServiceImpl implements MailService {
    private final MailRepository mailRepository;
    private final ApplicationContext applicationContext;
    private final JobLauncher jobLauncher;

    @Transactional
    public MailDto.Response create(MailDto.Request request) {
        return MailDto.Response.toDto(mailRepository.save(request.toEntity()));
    }

    public List<MailDto.Response> findAll() {
        return mailRepository.findAll()
                .stream()
                .map(MailDto.Response::toDto)
                .toList();
    }

    public MailDto.Response findById(Long id) {
        Mail mail = mailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("메일을 찾을 수 없습니다: " + id));

        return MailDto.Response.toDto(mail);
    }

    @Transactional
    public void deleteById(Long id){
        Mail mail = mailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("메일을 찾을 수 없습니다: " + id));
        mailRepository.delete(mail);
    }

    @Transactional
    public void update(Long id, MailDto.Request request){
        Mail mail = mailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("메일을 찾을 수 없습니다: " + id));
        mail.update(request.getTitle(), request.getContent());
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendBulkMail(Long mailId) {
        Mail findMail = mailRepository.findById(mailId).orElseThrow(() ->
                new IllegalArgumentException(mailId + " 에 해당하는 메일을 찾을 수 없습니다."));
        startBulkMailJob(findMail.getId(), findMail.getTitle(), findMail.getContent());
    }

    private void startBulkMailJob(Long mailId, String title, String message) {
        Job findJob = applicationContext.getBean("mailJob", Job.class);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("mailId", mailId)
                .addString("mailSubject", title)
                .addString("mailMessage", message)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(findJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }
}
