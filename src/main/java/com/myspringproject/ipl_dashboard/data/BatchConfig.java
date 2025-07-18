package com.myspringproject.ipl_dashboard.data;

import javax.sql.DataSource;
import com.myspringproject.ipl_dashboard.model.Match;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

        private final String[] FIELD_NAMES = new String[] { "id", "season", "city", "date", "match_type",
                        "player_of_match",
                        "venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
                        "target_runs", "target_overs", "super_over", "method", "umpire1", "umpire2" };

        @Bean
        public FlatFileItemReader<MatchInput> reader() {
                return new FlatFileItemReaderBuilder<MatchInput>()
                                .name("personItemReader")
                                .resource(new ClassPathResource("match_data.csv"))
                                .delimited()
                                .names(FIELD_NAMES)
                                .targetType(MatchInput.class)
                                .build();
        }

        // @Bean
        // public MatchDataProcessor processor() {
        // return new MatchDataProcessor();
        // }

        @Bean
        public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
                return new JdbcBatchItemWriterBuilder<Match>()
                                .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1_id, team2_id, toss_winner, toss_decision, match_winner_id, result, result_margin, umpire1, umpire2) "
                                                + " VALUES (:id, :city, :date, :playerOfMatch, :venue, :team1.id, :team2.id, :tossWinner, :tossDecision, :matchWinner.id, :result, :resultMargin, :umpire1, :umpire2)")
                                .dataSource(dataSource)
                                // .beanMapped()
                                .itemSqlParameterSourceProvider(new MatchItemSqlParameterSourceProvider())
                                .build();
        }

        @Bean
        public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
                return new JobBuilder("importUserJob", jobRepository)
                                .listener(listener)
                                .start(step1)
                                .build();
        }

        @Bean
        public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                        FlatFileItemReader<MatchInput> reader, MatchDataProcessor processor,
                        JdbcBatchItemWriter<Match> writer) {
                return new StepBuilder("step1", jobRepository)
                                .<MatchInput, Match>chunk(3, transactionManager)
                                .reader(reader)
                                .processor(processor)
                                .writer(writer)
                                .build();
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
                JpaTransactionManager transactionManager = new JpaTransactionManager();
                transactionManager.setEntityManagerFactory(entityManagerFactory);
                return transactionManager;
        }
}