package com.ascariaa.library.job;

import com.ascariaa.library.service.BorrowRecordService;
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
public class LibraryStatsJob extends QuartzJobBean {

    private final BorrowRecordService borrowRecordService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();

        if (!dataMap.containsKey("reportName")) {
            throw new JobExecutionException("Missing reportName parameter", false);
        }

        String reportName = dataMap.getString("reportName");
        boolean includeInactive = dataMap.getBooleanValue("includeInactive");

        try {
            log.info("Generating report: {}, inactive records included: {}", reportName, includeInactive);
            borrowRecordService.generateStatsReport(reportName, includeInactive);
        } catch (Exception e) {
            log.error("Failed to generate report", e);
            throw new JobExecutionException(e, false);
        }
    }
}