package com.ascariaa.library.config;


import com.ascariaa.library.job.LibraryStatsJob;
import com.ascariaa.library.job.OverdueReminderJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail reminderDetail(){
        return JobBuilder.newJob(OverdueReminderJob.class)
                .withIdentity("reminderJob", "reminders")
                .usingJobData("daysOverdue", 14)
                .usingJobData("messageType", "standard")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger reminderTrigger(JobDetail reminderDetail){
        return TriggerBuilder.newTrigger()
                .forJob(reminderDetail)
                .withIdentity("dailyReminderTrigger", "reminders")
                .usingJobData("daysOverdue", 30)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(9, 0)
                        .inTimeZone(TimeZone.getTimeZone("UTC"))
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }

    @Bean
    public JobDetail statsDetail(){
        return JobBuilder.newJob(LibraryStatsJob.class)
                .withIdentity("statsJob", "reports")
                .usingJobData("reportName", "generalStats")
                .usingJobData("includeInactive", false)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailyStatsTrigger(JobDetail statsDetail){
        return TriggerBuilder.newTrigger()
                .forJob(statsDetail)
                .withIdentity("dailyStatsTrigger", "reports")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 23 * * ?")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Kyiv"))
                        .withMisfireHandlingInstructionDoNothing())
                .build();
    }

    @Bean
    public Trigger manualStatsTrigger(JobDetail statsDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(statsDetail)
                .withIdentity("manualStatsTrigger", "reports")
                .usingJobData("reportName", "ManualAdminStats")
                .usingJobData("includeInactive", true)
                .startNow()
                .build();
    }

}
