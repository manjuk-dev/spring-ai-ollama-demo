package com.example.demo.features.ChatMemory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ChatMemoryCleanupService {

    private final JdbcTemplate jdbcTemplate;

    public ChatMemoryCleanupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This method runs automatically.
     * Cron format: "0 0 0 * * *" means "at 00:00:00 every day"
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void autoPurgeOldMessages() {
        // H2 specific SQL to delete records older than 7 days
        String sql = "DELETE FROM SPRING_AI_CHAT_MEMORY WHERE TIMESTAMP < DATEADD('DAY', -7, CURRENT_TIMESTAMP())";

        try {
            int rowsDeleted = jdbcTemplate.update(sql);
            if (rowsDeleted > 0) {
                System.out.println("********** Auto-Cleanup**********: Removed " + rowsDeleted + " messages older than 7 days.");
            }
        } catch (Exception e) {
            System.err.println("********** Cleanup failed**********: " + e.getMessage());
        }
    }
}
