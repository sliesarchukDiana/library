package com.ascariaa.library.job;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class QuartzIntegrationTest {

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private Scheduler scheduler;

    @Test
    void triggerParametersOverrideJobDefaults() throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey("reminderJob", "reminders"));
        Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey("dailyReminderTrigger", "reminders"));

        assertNotNull(jobDetail);
        assertNotNull(trigger);

        JobDataMap mergedMap = new JobDataMap();
        mergedMap.putAll(jobDetail.getJobDataMap());
        mergedMap.putAll(trigger.getJobDataMap());

        assertEquals(30, mergedMap.getInt("daysOverdue"));
        assertEquals("standard", mergedMap.getString("messageType"));
    }

    @Test
    void cronTriggerHasExplicitTimeZoneAndMisfireInstruction() throws SchedulerException {
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("dailyReminderTrigger", "reminders"));

        assertEquals(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW, trigger.getMisfireInstruction());

        assertEquals(TimeZone.getTimeZone("UTC"), trigger.getTimeZone());
    }
}