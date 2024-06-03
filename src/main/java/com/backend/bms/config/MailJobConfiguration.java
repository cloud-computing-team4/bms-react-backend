package com.backend.bms.config;

import com.backend.bms.domain.User;
import com.backend.bms.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
public class MailJobConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MailJobConfiguration.class);

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
                .<User, User>chunk(20, platformTransactionManager) // 청크 크기 20
                .reader(mailItemReader()) // User 객체를 읽어옴
                .writer(mailItemWriter(null, null)) // User 객체로 메일을 보내는 것 수행
                .build();
    }

    @Bean
    public ItemReader<User> mailItemReader() {
        return new JdbcCursorItemReaderBuilder<User>()
                .name("JdbcCursorItemReader")
                .fetchSize(20)
                .sql("SELECT id, name, email FROM user")
                .rowMapper(new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return User.builder()
                                .name(rs.getString("name"))
                                .email(rs.getString("email"))
                                .build();
                    }
                })
                .dataSource(dataSource)
                .build();
    }

    @Bean
    @JobScope
    public ItemWriter<User> mailItemWriter(@Value("#{jobParameters['mailSubject']}") String subject,
                                           @Value("#{jobParameters['mailMessage']}") String mailMessage) {
        return new ItemWriter<User>() {
            @Override
            public void write(Chunk<? extends User> items) throws Exception {
                String[] emails = items.getItems().stream().map(User::getEmail).toArray(String[]::new);
                long startTime = System.currentTimeMillis();
                gmailService.sendEmail(emails, subject, mailMessage);
                long endTime = System.currentTimeMillis();
                logger.info("Chunk sent in {} ms", (endTime - startTime));
            }
        };
    }
}
