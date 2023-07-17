package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.schedule.mapper.ScheduleMapper;
import com.cudo.pixelviewer.vo.LedPlayScheduleVo;
import com.cudo.pixelviewer.vo.LightScheduleVo;
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
        List<PowerScheduleVo> powerScheduleList = scheduleMapper.selectPowerSchedule(nowDate);

        // 전원 스케줄 등록
        powerSchedule(powerScheduleList, nowDate);

        // 밝기 스케줄 조회
        List<LightScheduleVo> lightScheduleList = scheduleMapper.selectLightSchedule(nowDate);

        // 밝기 스케줄 등록
        lightSchedule(lightScheduleList, nowDate);

        // LED 플레이리스트 스케줄 조회
        List<LedPlayScheduleVo> ledPlaySchedule = scheduleMapper.selectLedPlayListSchedule(nowDate);

        // LED 플레이리스트 스케줄 등록
        ledPlaySchedule(ledPlaySchedule, nowDate);

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

            log.info("Power Schedule Register Id : {}", String.valueOf(powerDataMap.get(DATA_MAP_KEY.getCode())) + powerInfo.getScheduleId());
        } else {
            log.info("This is the time when schedule registration is not possible. power ScheduleId : {}", powerInfo.getScheduleId());
        }
    }

    /**
     * * 밝기 스케줄 설정
     */
    private void lightSchedule(List<LightScheduleVo> lightSchedule, String nowDate) throws SchedulerException {
        for (LightScheduleVo lightInfo : lightSchedule) {

            if (checkDay(nowDate, lightInfo.getRunDayWeek())) {
                List<String> lightTime = Arrays.asList(lightInfo.getRuntime().split(":"));

                // 밝기 스케줄 등록
                if (lightTime.size() > 0) {
                    JobDataMap lightDataMap = new JobDataMap();

                    lightDataMap.put(LIGHT.getCode(), LIGHT.getValue());
                    lightDataMap.put(LIGHT.getValue(), Float.parseFloat(lightInfo.getBrightnessVal()) / 100);

                    setLightSchedule(nowDate, lightInfo, lightTime, lightDataMap);
                }
            }
        }
    }

    /**
     * * Led 플레이리스트 스케줄 설정
     */
    private void ledPlaySchedule(List<LedPlayScheduleVo> ledPlaySchedule, String nowDate) throws SchedulerException {
        for (LedPlayScheduleVo ledPlayScheduleInfo : ledPlaySchedule) {

            if (checkDay(nowDate, ledPlayScheduleInfo.getRunDayWeek())) {
                List<String> startTime = Arrays.asList(ledPlayScheduleInfo.getTimeStart().split(":")),
                        endTime = Arrays.asList(ledPlayScheduleInfo.getTimeEnd().split(":"));

                // 영상 시작 스케줄 등록
                if (startTime.size() > 0) {
                    JobDataMap powerOnDataMap = new JobDataMap();

                    powerOnDataMap.put(LED_PLAY_LIST_START.getCode(), LED_PLAY_LIST_START.getValue());
                    setLedPlayListSchedule(nowDate, ledPlayScheduleInfo, startTime, powerOnDataMap);
                }

                // 영상 종료 스케줄 등록
                if (endTime.size() > 0) {
                    JobDataMap powerOffDataMap = new JobDataMap();

                    powerOffDataMap.put(LED_PLAY_LIST_END.getCode(), LED_PLAY_LIST_END.getValue());

                    setLedPlayListSchedule(nowDate, ledPlayScheduleInfo, endTime, powerOffDataMap);
                }
            }
        }

    }

    /**
     * * Led 플레이리스트 스케줄 job, trigger 설정
     */
    private void setLedPlayListSchedule(String nowDate, LedPlayScheduleVo ledPlayInfo, List<String> ledPlayTime, JobDataMap LedPlayDataMap) throws SchedulerException {
        LocalTime powerOnOffTime = LocalTime.of(Integer.parseInt(ledPlayTime.get(0)), Integer.parseInt(ledPlayTime.get(1)), Integer.parseInt(ledPlayTime.get(2)));
        LocalTime nowTime = LocalTime.now();

        if (powerOnOffTime.isAfter(nowTime)) {
            JobDetail powerJob = JobBuilder.newJob(ScheduleJob.class)
                    .withIdentity(String.valueOf(LedPlayDataMap.get(DATA_MAP_KEY.getCode())) + ledPlayInfo.getScheduleId())
                    .usingJobData(LedPlayDataMap)
                    .requestRecovery(true)
                    .build();

            Trigger powerTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(LedPlayDataMap.get(DATA_MAP_KEY.getCode())) + ledPlayInfo.getScheduleId())
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(cronExpression(nowDate, ledPlayTime)))
                    .build();

            scheduler.scheduleJob(powerJob, powerTrigger);

            log.info("Led PlayList Schedule Register Id : {}", String.valueOf(LedPlayDataMap.get(DATA_MAP_KEY.getCode())) + ledPlayInfo.getScheduleId());
        } else {
            log.info("This is the time when schedule registration is not possible. Led PlayList ScheduleId : {}", ledPlayInfo.getScheduleId());
        }
    }

    /**
     * * 밝기 스케줄 job, trigger 설정
     */
    private void setLightSchedule(String nowDate, LightScheduleVo lightInfo, List<String> lightTime, JobDataMap lightDataMap) throws SchedulerException {
        LocalTime lightScheduleTime = LocalTime.of(Integer.parseInt(lightTime.get(0)), Integer.parseInt(lightTime.get(1)), Integer.parseInt(lightTime.get(2)));
        LocalTime nowTime = LocalTime.now();

        if (lightScheduleTime.isAfter(nowTime)) {
            JobDetail lightJob = JobBuilder.newJob(ScheduleJob.class)
                    .withIdentity(String.valueOf(lightDataMap.get(DATA_MAP_KEY.getCode())) + lightInfo.getListId())
                    .usingJobData(lightDataMap)
                    .requestRecovery(true)
                    .build();

            Trigger lightTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(lightDataMap.get(DATA_MAP_KEY.getCode())) + lightInfo.getListId())
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(cronExpression(nowDate, lightTime)))
                    .build();

            scheduler.scheduleJob(lightJob, lightTrigger);

            log.info("Light Schedule Register Id : {}", String.valueOf(lightDataMap.get(DATA_MAP_KEY.getCode())) + lightInfo.getListId());
        } else {
            log.info("This is the time when schedule registration is not possible. Light ScheduleId : {}", lightInfo.getListId());
        }
    }

    /**
     * * 요일이 수행 날짜인지 체크
     */
    private boolean checkDay(String nowDate, String runDayWeek) {

        // null일 경우 모든 요일 실행
        if (runDayWeek == null) {
            return true;
        }

        if (nowDate.length() > 8) {
            String day = nowDate.substring(8),
                    month = nowDate.substring(5, 7),
                    year = nowDate.substring(0, 4);

            Set<String> daysOfWeek = new HashSet<>(Arrays.asList(runDayWeek.split(",")));
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

            String dayOfWeek = date.getDayOfWeek().getValue() == 7 ? "0" : String.valueOf(date.getDayOfWeek().getValue());

            // 요일이 맞을 경우
            return daysOfWeek.contains(dayOfWeek);
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
