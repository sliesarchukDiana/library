package com.ascariaa.library.job;

import com.ascariaa.library.service.AsyncLibraryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class OverdueReminderJob extends QuartzJobBean {

    private final AsyncLibraryService asyncLibraryService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();

        if (!dataMap.containsKey("messageType")) {
            throw new JobExecutionException("Missing messageType parameter", false);
        }

        int daysOverdue = dataMap.getInt("daysOverdue");
        String messageType = dataMap.getString("messageType");

        try {
            log.info("Processing overdue reminders: Days overdue threshold: {}, Message type: {}", daysOverdue, messageType);
            asyncLibraryService.processOverdueReminders(daysOverdue, messageType);
        } catch (Exception e) {
            log.error("Failed to execute overdue reminder", e);
            throw new JobExecutionException(e, false);
        }
    }
}