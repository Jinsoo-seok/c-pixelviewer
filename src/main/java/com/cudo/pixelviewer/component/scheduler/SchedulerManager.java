package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.vo.LightScheduleVo;
import com.cudo.pixelviewer.vo.PowerScheduleVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerManager {
    final SchedulerFactoryBean schedulerFactoryBean;
    final String DEFAULT = "DEFAULT";

    final Scheduler scheduler;

    /**
     * * 스케줄 삭제
     */
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

    /**
     * * 스케줄 생성/수정
     */
    public void setJob(Object scheduleInfo, String type, boolean update) throws SchedulerException, ParseException {
        if (type.equals(POWER_ON.getValue()) || type.equals(POWER_OFF.getValue())) { // 전원 스케줄

            PowerScheduleVo powerSchedule = (PowerScheduleVo) scheduleInfo;
            List<String> scheduleTime = new ArrayList<>();

            if (powerSchedule.getTimePwrOn().length() == 6) {
                scheduleTime.add(powerSchedule.getTimePwrOn().substring(0, 2));
                scheduleTime.add(powerSchedule.getTimePwrOn().substring(2, 4));
                scheduleTime.add(powerSchedule.getTimePwrOn().substring(4));
            }

            if (checkDayTime(powerSchedule.getSchStartDate(), powerSchedule.getSchEndDate(), scheduleTime, powerSchedule.getRunDayWeek())) {

                // 전원 스케줄 수정
                if (update) {
                    boolean scheduleExistCheck = updateTrigger(powerSchedule.getScheduleId(), scheduleTime, type);

                    if (!scheduleExistCheck) {
                        JobDataMap jobDataMap = new JobDataMap();
                        jobDataMap.put(DATA_MAP_KEY.getCode(), type);

                        setSchedule(powerSchedule.getScheduleId(), scheduleTime, jobDataMap);
                    }
                } else { // 전원 스케줄 등록
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put(DATA_MAP_KEY.getCode(), type);

                    setSchedule(powerSchedule.getScheduleId(), scheduleTime, jobDataMap);
                }
            }

        } else if (type.equals(LIGHT.getValue())) { // 밝기 스케줄 등록
            List<LightScheduleVo> lightScheduleList = (List<LightScheduleVo>) scheduleInfo;

            for (LightScheduleVo lightSchedule : lightScheduleList) {
                List<String> scheduleTime = new ArrayList<>();

                if (lightSchedule.getRuntime().length() == 6) {
                    scheduleTime.add(lightSchedule.getRuntime().substring(0, 2));
                    scheduleTime.add(lightSchedule.getRuntime().substring(2, 4));
                    scheduleTime.add(lightSchedule.getRuntime().substring(4));
                }
                if (checkDayTime(lightSchedule.getSchStartDate(), lightSchedule.getSchEndDate(), scheduleTime, lightSchedule.getRunDayWeek())) {
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put(DATA_MAP_KEY.getCode(), type);
                    jobDataMap.put(LIGHT.getValue(), Float.parseFloat(lightSchedule.getBrightnessVal()) / 100);

                    setSchedule(lightSchedule.getListId(), scheduleTime, jobDataMap);
                }
            }
        } else if (type.equals(LED_PLAY_LIST.getValue())) {
            System.out.println("LED 영상 play");
        }
    }

    /**
     * * 같은 날, 이후의 시간인지 체크
     */
    private boolean checkDayTime(String scheduleStartDate, String scheduleEndDate, List<String> scheduleTime, String runDayWeek) {
        if (scheduleStartDate.length() == 8 && scheduleEndDate.length() == 8 && scheduleTime.size() == 3) {

            Set<String> daysOfWeek = new HashSet<>();

            if (runDayWeek == null) { // null일 경우 모든 요일 수행
                for (int i = 0; i < 7; i++) {
                    daysOfWeek.add(String.valueOf(i));
                }
            } else {
                daysOfWeek = new HashSet<>(Arrays.asList(runDayWeek.split(",")));
            }

            LocalDate startDate = LocalDate.of(Integer.parseInt(scheduleStartDate.substring(0, 4)),
                    Integer.parseInt(scheduleStartDate.substring(4, 6)), Integer.parseInt(scheduleStartDate.substring(6))),

                    endDate = LocalDate.of(Integer.parseInt(scheduleEndDate.substring(0, 4)),
                            Integer.parseInt(scheduleEndDate.substring(4, 6)), Integer.parseInt(scheduleEndDate.substring(6))),

                    nowDate = LocalDate.now();

            LocalTime scheduleLocalTime = LocalTime.of(Integer.parseInt(scheduleTime.get(0)), Integer.parseInt(scheduleTime.get(1)), Integer.parseInt(scheduleTime.get(2)));
            LocalTime nowTime = LocalTime.now();

            String dayOfWeek = nowDate.getDayOfWeek().getValue() == 7 ? "0" : String.valueOf(nowDate.getDayOfWeek().getValue());

            // 요일이 맞고 시간이 이후 일 경우
            return (daysOfWeek.contains(dayOfWeek) && scheduleLocalTime.isAfter(nowTime)
                    && (startDate.equals(nowDate) || startDate.isBefore(nowDate)) && (endDate.equals(nowDate) || endDate.isAfter(nowDate)));
        }

        return false;
    }

    /**
     * * 스케줄 job, trigger 설정
     */
    private void setSchedule(Long scheduleId, List<String> scheduleTime, JobDataMap jobDataMap) throws SchedulerException {

        JobDetail lightJob = JobBuilder.newJob(ScheduleJob.class)
                .withIdentity(jobDataMap.get(DATA_MAP_KEY.getCode()) + String.valueOf(scheduleId))
                .usingJobData(jobDataMap)
                .requestRecovery(true)
                .build();

        Trigger lightTrigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDataMap.get(DATA_MAP_KEY.getCode()) + String.valueOf(scheduleId))
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(cronExpression(scheduleTime)))
                .build();

        scheduler.scheduleJob(lightJob, lightTrigger);

        log.info("{} Schedule Register Id : {}", jobDataMap.get(DATA_MAP_KEY.getCode()), jobDataMap.get(DATA_MAP_KEY.getCode()) + String.valueOf(scheduleId));
    }

    /**
     * * trigger 수정
     */
    private boolean updateTrigger(Long scheduleId, List<String> scheduleTime, String type) throws SchedulerException, ParseException {

        CronTriggerImpl cronTrigger = (CronTriggerImpl) scheduler.getTrigger(TriggerKey.triggerKey(type + scheduleId));

        if (cronTrigger != null) {
            cronTrigger.setCronExpression(cronExpression(scheduleTime));

            scheduler.rescheduleJob(TriggerKey.triggerKey(type + scheduleId), cronTrigger);

            log.info("{} Schedule Re-Register Id : {}", type, type + scheduleId);
            return true;
        }

        return false;

    }

    /**
     * * cron 표현식 변환
     */
    private String cronExpression(List<String> nowTime) {
        String cron = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String nowDate = dateFormat.format(new Date());

        if (nowTime.size() == 3) {
            String day = nowDate.substring(8),
                    month = nowDate.substring(5, 7),
                    year = nowDate.substring(0, 4);

            cron = Integer.parseInt(nowTime.get(2)) + " " + Integer.parseInt(nowTime.get(1)) + " " + Integer.parseInt(nowTime.get(0))
                    + " " + Integer.parseInt(day) + " " + Integer.parseInt(month) + " ? " + Integer.parseInt(year);
        }

        return cron;
    }
}
