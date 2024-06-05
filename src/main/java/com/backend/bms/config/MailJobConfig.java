package com.backend.bms.config;

import com.backend.bms.domain.User;
import com.backend.bms.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

@Configuration
@EnableBatchProcessing
@EnableAsync
public class MailJobConfig {
    private static final Logger logger = LoggerFactory.getLogger(MailJobConfig.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GmailService gmailService;

    @Bean
    public Job mailJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("mailJob", jobRepository)
                .start(sendBulkMailStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step sendBulkMailStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("sendMailStep", jobRepository)
                .<User, User>chunk(500, platformTransactionManager) // 청크 크기 조정
                .reader(mailItemReader())
                .writer(mailItemWriter(null, null))
                .taskExecutor(taskExecutor()) // 병렬 처리
                .build();
    }

    @Bean
    public ItemReader<User> mailItemReader() {
        return new JdbcPagingItemReaderBuilder<User>()
                .name("JdbcPagingItemReader")
                .dataSource(dataSource)
                .fetchSize(500) // 페이지 크기 조정
                .rowMapper(new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return User.builder()
                                .name(rs.getString("name"))
                                .email(rs.getString("email"))
                                .build();
                    }
                })
                .selectClause("SELECT id, name, email")
                .fromClause("FROM user")
                .sortKeys(Collections.singletonMap("id", Order.ASCENDING)) // 페이징을 위한 정렬 키
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<User> mailItemWriter(@Value("#{jobParameters['mailSubject']}") String subject,
                                           @Value("#{jobParameters['mailMessage']}") String mailMessage) {
        return items -> {
            items.getItems().parallelStream().forEach(user -> {
                try {
                    long startTime = System.currentTimeMillis();
                    gmailService.sendEmail(new String[]{user.getEmail()}, subject, mailMessage);
                    long endTime = System.currentTimeMillis();
                    logger.info("Email to {} sent in {} ms", user.getEmail(), (endTime - startTime));
                } catch (Exception e) {
                    logger.error("Failed to send email to {}", user.getEmail(), e);
                }
            });
        };
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20); // 코어 스레드 수
        taskExecutor.setMaxPoolSize(40); // 최대 스레드 수
        taskExecutor.setQueueCapacity(200); // 큐 용량
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
