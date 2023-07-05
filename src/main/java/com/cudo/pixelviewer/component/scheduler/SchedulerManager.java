package com.cudo.pixelviewer.component.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerManager {
    final SchedulerFactoryBean schedulerFactoryBean;
    final String DEFAULT = "DEFAULT";

    public void deleteJob(String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        JobKey jobKey = new JobKey(jobName, DEFAULT);

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("Success delete schedule. >> {}", jobKey);
        } else {
            log.info("No Exist schedule >> {}", jobKey);
        }
    }
}
