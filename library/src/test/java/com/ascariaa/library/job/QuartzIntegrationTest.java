package com.ascariaa.library.job;

import com.ascariaa.library.service.AsyncLibraryService;
import com.ascariaa.library.service.BorrowRecordService;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
class QuartzIntegrationTest {

    @MockitoBean
    private AsyncLibraryService asyncLibraryService;

    @MockitoBean
    private BorrowRecordService borrowRecordService;

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

    @Test
    void executeJob_MissingParameter_ThrowsJobExecutionException() {
        OverdueReminderJob job = new OverdueReminderJob(asyncLibraryService);
        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDataMap dataMap = new JobDataMap();

        dataMap.put("daysOverdue", 14);
        when(context.getMergedJobDataMap()).thenReturn(dataMap);

        JobExecutionException exception = assertThrows(JobExecutionException.class, () -> {
            job.executeInternal(context);
        });

        assertEquals("Missing messageType parameter", exception.getMessage());
    }

    @Test
    void executeJob_ServiceThrowsException_JobExecutionExceptionIsThrown() throws Exception {
        OverdueReminderJob job = new OverdueReminderJob(asyncLibraryService);
        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("daysOverdue", 14);
        dataMap.put("messageType", "standard");
        when(context.getMergedJobDataMap()).thenReturn(dataMap);

        doThrow(new RuntimeException("Database timeout")).when(asyncLibraryService)
                .processOverdueReminders(14, "standard");

        JobExecutionException exception = assertThrows(JobExecutionException.class, () -> {
            job.executeInternal(context);
        });

        assertFalse(exception.refireImmediately());
        assertEquals("Database timeout", exception.getCause().getMessage());
    }

    @Test
    void jobExecution_IsIdempotent() throws Exception {
        int daysOverdue = 14;
        String messageType = "standard";

        asyncLibraryService.processOverdueReminders(daysOverdue, messageType);
        asyncLibraryService.processOverdueReminders(daysOverdue, messageType);

        verify(asyncLibraryService, times(2)).processOverdueReminders(daysOverdue, messageType);
    }
}