package com.cudo.pixelviewer.config.scheduler;

import com.cudo.pixelviewer.component.scheduler.PeriodicJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScheduleConfig {
    final Scheduler scheduler;

    @PostConstruct
    public void run() {
        JobDetail detail = JobBuilder.newJob(PeriodicJob.class)
                .withIdentity(PeriodicJob.class.getName())
                .requestRecovery(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(PeriodicJob.class.getName())
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 0 * * ?"))
                .build();
        try {
            scheduler.scheduleJob(detail, trigger);
        } catch (SchedulerException e) {
            log.error(e.toString());
        }

    }

}
