package com.zeki.kisvolcano.domain._common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CommonScheduler {
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 60000 * 60)
    public void connection() {
        jdbcTemplate.execute("SELECT 1");
        log.info("DB Connection Check");
    }
}
