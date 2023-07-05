package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.schedule.mapper.ScheduleMapper;
import com.cudo.pixelviewer.vo.PowerScheduleVo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;

@RequiredArgsConstructor
@Slf4j
public class PeriodicJob implements Job {
    final ScheduleMapper scheduleMapper;
    final Scheduler scheduler;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String nowDate = dateFormat.format(new Date());

        Map<String, Object> param = new HashMap<>();

        param.put("nowDate", nowDate);

        // 전원 스케줄 조회
        List<PowerScheduleVo> powerScheduleList = scheduleMapper.selectPowerSchedule(param);

        // 전원 스케줄 등록
        powerSchedule(powerScheduleList, nowDate);
    }

    /**
     * * 전원 스케줄 설정
     */
    private void powerSchedule(List<PowerScheduleVo> powerSchedule, String nowDate) throws SchedulerException {
        for (PowerScheduleVo powerInfo : powerSchedule) {

            if (checkDay(nowDate, powerInfo.getRunDayWeek())) {
                List<String> powerOnTime = Arrays.asList(powerInfo.getTimePwrOn().split(":")),
                        powerOffTime = Arrays.asList(powerInfo.getTimePwrOff().split(":"));

                // 전원 ON 스케줄 등록
                if (powerOnTime.size() > 0) {
                    JobDataMap powerOnDataMap = new JobDataMap();

                    powerOnDataMap.put(POWER_ON.getCode(), POWER_ON.getValue());
                    setPowerSchedule(nowDate, powerInfo, powerOnTime, powerOnDataMap);
                }

                // 전원 OFF 스케줄 등록
                if (powerOffTime.size() > 0) {
                    JobDataMap powerOffDataMap = new JobDataMap();

                    powerOffDataMap.put(POWER_OFF.getCode(), POWER_OFF.getValue());

                    setPowerSchedule(nowDate, powerInfo, powerOffTime, powerOffDataMap);
                }
            }
        }

    }

    /**
     * * 전원 스케줄 job, trigger 설정
     */
    private void setPowerSchedule(String nowDate, PowerScheduleVo powerInfo, List<String> powerTime, JobDataMap powerDataMap) throws SchedulerException {
        LocalTime powerOnOffTime = LocalTime.of(Integer.parseInt(powerTime.get(0)), Integer.parseInt(powerTime.get(1)), Integer.parseInt(powerTime.get(2)));
        LocalTime nowTime = LocalTime.now();

        if (powerOnOffTime.isAfter(nowTime)) {
            JobDetail powerJob = JobBuilder.newJob(ScheduleJob.class)
                    .withIdentity(String.valueOf(powerDataMap.get(DATA_MAP_KEY.getCode())) + powerInfo.getScheduleId())
                    .usingJobData(powerDataMap)
                    .requestRecovery(true)
                    .build();

            Trigger powerTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(powerDataMap.get(DATA_MAP_KEY.getCode())) + powerInfo.getScheduleId())
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(cronExpression(nowDate, powerTime)))
                    .build();

            scheduler.scheduleJob(powerJob, powerTrigger);

            log.info("Schedule Register Id : {}", String.valueOf(powerDataMap.get(DATA_MAP_KEY.getCode())) + powerInfo.getScheduleId());
        } else {
            log.info("This is the time when schedule registration is not possible");
        }
    }

    /**
     * * 요일이 수행 날짜인지 체크
     */
    private boolean checkDay(String nowDate, String runDayWeek) {

        if (nowDate.length() > 8) {
            String day = nowDate.substring(8),
                    month = nowDate.substring(5, 7),
                    year = nowDate.substring(0, 4);

            Set<String> daysOfWeek = new HashSet<>(Arrays.asList(runDayWeek.split(",")));
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

            // 요일이 맞을 경우
            return daysOfWeek.contains(String.valueOf(date.getDayOfWeek().getValue() - 1));
        }
        return false;
    }

    /**
     * * cron 표현식 변환
     */
    private String cronExpression(String nowDate, List<String> nowTime) {
        String cron = "";

        if (nowDate.length() > 8 && nowTime.size() == 3) {
            String day = nowDate.substring(8), month = nowDate.substring(5, 7), year = nowDate.substring(0, 4);

            cron = Integer.parseInt(nowTime.get(2)) + " " + Integer.parseInt(nowTime.get(1)) + " " + Integer.parseInt(nowTime.get(0))
                    + " " + Integer.parseInt(day) + " " + Integer.parseInt(month) + " ? " + Integer.parseInt(year);
        }

        return cron;
    }
}
