package com.ascariaa.library.job;

import com.ascariaa.library.service.BorrowRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryStatsJobTest {

    @Mock private BorrowRecordService borrowRecordService;
    @Mock private JobExecutionContext context;
    @InjectMocks private LibraryStatsJob job;

    @Test
    void executeInternal_missingReportName_throwsException() {
        when(context.getMergedJobDataMap()).thenReturn(new JobDataMap());
        assertThrows(JobExecutionException.class, () -> job.executeInternal(context));
    }

    @Test
    void executeInternal_validParams_callsService() throws JobExecutionException {
        JobDataMap map = new JobDataMap();
        map.put("reportName", "testReport");
        map.put("includeInactive", true);
        when(context.getMergedJobDataMap()).thenReturn(map);

        job.executeInternal(context);
        verify(borrowRecordService).generateStatsReport("testReport", true);
    }
}