package com.ascariaa.library.job;


import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
public class LibraryStatsJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context){
        JobDataMap dataMap = context.getMergedJobDataMap();
        String reportName = dataMap.getString("reportName");
        boolean includeInactive = dataMap.getBooleanValue("includeInactive");
        log.info("Generating report: {}, inactive records included: {}", reportName, includeInactive);
    }
}
