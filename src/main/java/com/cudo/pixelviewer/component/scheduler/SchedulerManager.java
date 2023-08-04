package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.operate.service.PresetService;
import com.cudo.pixelviewer.vo.*;
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
import java.util.stream.Collectors;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerManager {

    public enum ScheduleStatus {
        NO_SCHEDULE,
        CALL_WITHOUT_SCHEDULE,
        REGISTER_SCHEDULE
    }

    final SchedulerFactoryBean schedulerFactoryBean;

    final PresetMapper presetMapper;

    final PlaylistMapper playlistMapper;

    final PresetService presetService;

    final String DEFAULT = "DEFAULT";

    final Scheduler scheduler;

    final static String PRESET_ID = "presetId";

    final static String LAYER_ID = "layerId";

    final static String PLAYLIST_ID = "playListId";

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
    public void setJob(Object scheduleInfo, String type, boolean update) throws SchedulerException, ParseException, InterruptedException {
        if (type.equals(POWER_ON.getValue()) || type.equals(POWER_OFF.getValue())) { // 전원 스케줄

            PowerScheduleVo powerSchedule = (PowerScheduleVo) scheduleInfo;
            List<String> scheduleTime = new ArrayList<>();

            if (type.equals(POWER_ON.getValue())) {
                if (powerSchedule.getTimePwrOn().length() == 6) {
                    scheduleTime.add(powerSchedule.getTimePwrOn().substring(0, 2));
                    scheduleTime.add(powerSchedule.getTimePwrOn().substring(2, 4));
                    scheduleTime.add(powerSchedule.getTimePwrOn().substring(4));
                }
            } else {
                if (powerSchedule.getTimePwrOff().length() == 6) {
                    scheduleTime.add(powerSchedule.getTimePwrOff().substring(0, 2));
                    scheduleTime.add(powerSchedule.getTimePwrOff().substring(2, 4));
                    scheduleTime.add(powerSchedule.getTimePwrOff().substring(4));
                }
            }

            insertAndUpdateSchedule(type, update, scheduleTime, powerSchedule.getSchStartDate(), powerSchedule.getSchEndDate(), powerSchedule.getRunDayWeek(), powerSchedule.getScheduleId(), null);

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
        } else if (type.equals(LED_PLAY_LIST_START.getValue())) { // LED 플레이 영상 시작 스케줄
            LedPlayScheduleVo ledPlayScheduleVo = (LedPlayScheduleVo) scheduleInfo;

            // 시작 시간
            List<String> scheduleStartTime = new ArrayList<>();

            if (ledPlayScheduleVo.getTimeStart().length() == 6) {
                scheduleStartTime.add(ledPlayScheduleVo.getTimeStart().substring(0, 2));
                scheduleStartTime.add(ledPlayScheduleVo.getTimeStart().substring(2, 4));
                scheduleStartTime.add(ledPlayScheduleVo.getTimeStart().substring(4));
            }

            // 종료 시간
            List<String> scheduleEndTime = new ArrayList<>();

            if (ledPlayScheduleVo.getTimeEnd().length() == 6) {
                scheduleEndTime.add(ledPlayScheduleVo.getTimeEnd().substring(0, 2));
                scheduleEndTime.add(ledPlayScheduleVo.getTimeEnd().substring(2, 4));
                scheduleEndTime.add(ledPlayScheduleVo.getTimeEnd().substring(4));
            }

            ScheduleStatus scheduleStatus = checkLedPlayStartTime(ledPlayScheduleVo.getSchStartDate(), ledPlayScheduleVo.getSchEndDate(),
                    scheduleStartTime, scheduleEndTime, ledPlayScheduleVo.getRunDayWeek());


            if (scheduleStatus.equals(ScheduleStatus.REGISTER_SCHEDULE)) {
                boolean scheduleExistCheck = update && updateTrigger(ledPlayScheduleVo.getScheduleId(), scheduleStartTime, type);

                if (!scheduleExistCheck) {
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put(DATA_MAP_KEY.getCode(), type);
                    jobDataMap.put(PRESET_ID, ledPlayScheduleVo.getPresetId());
                    jobDataMap.put(LAYER_ID, ledPlayScheduleVo.getLayerId());
                    jobDataMap.put(PLAYLIST_ID, ledPlayScheduleVo.getPlaylistId());

                    setSchedule(ledPlayScheduleVo.getScheduleId(), scheduleStartTime, jobDataMap);
                }
            } else if (scheduleStatus.equals(ScheduleStatus.CALL_WITHOUT_SCHEDULE)) {
                if (update) {
                    deleteJob(type + ledPlayScheduleVo.getScheduleId());
                }

                PresetStatusRunVo usingPresetVo = presetMapper.getUsingPreset();
                Map<String, Object> presetRunMap = new HashMap<>();
                JobDataMap jobDataMap = new JobDataMap();

                jobDataMap.put(PRESET_ID, ledPlayScheduleVo.getPresetId());
                jobDataMap.put(LAYER_ID, ledPlayScheduleVo.getLayerId());
                jobDataMap.put(PLAYLIST_ID, ledPlayScheduleVo.getPlaylistId());

                // 재생중인 프리셋 영상 재생
                if (usingPresetVo.getPresetId().equals(Integer.parseInt(String.valueOf(ledPlayScheduleVo.getPresetId())))) {
                    presetRunMap = setPresetRunMap("apply", jobDataMap.get(PRESET_ID), jobDataMap);

                    System.out.println("재생중인 프리셋 영상 재생");
                } else { // 재생 중이 아닌 프리셋 영상 재생
                    presetRunMap = setPresetRunMap("play", jobDataMap.get(PRESET_ID), jobDataMap);

                    System.out.println("재생 중이 아닌 프리셋 영상 재생");
                }

                presetService.patchPresetRun(presetRunMap);
                presetMapper.patchPresetStatusSet(presetStatusMap(jobDataMap.get(PRESET_ID), "play"));
            }

        } else if (type.equals(LED_PLAY_LIST_END.getValue())) {  // LED 플레이 영상 종료 스케줄
            LedPlayScheduleVo ledPlayScheduleVo = (LedPlayScheduleVo) scheduleInfo;

            // 종료 시간
            List<String> scheduleEndTime = new ArrayList<>();

            if (ledPlayScheduleVo.getTimeEnd().length() == 6) {
                scheduleEndTime.add(ledPlayScheduleVo.getTimeEnd().substring(0, 2));
                scheduleEndTime.add(ledPlayScheduleVo.getTimeEnd().substring(2, 4));
                scheduleEndTime.add(ledPlayScheduleVo.getTimeEnd().substring(4));
            }

            insertAndUpdateSchedule(type, update, scheduleEndTime, ledPlayScheduleVo.getSchStartDate(), ledPlayScheduleVo.getSchEndDate(), ledPlayScheduleVo.getRunDayWeek(), ledPlayScheduleVo.getScheduleId(), ledPlayScheduleVo.getPresetId());
        }
    }

    private void insertAndUpdateSchedule(String type, boolean update, List<String> scheduleEndTime, String schStartDate, String schEndDate, String runDayWeek, Long scheduleId, Long presetId) throws SchedulerException, ParseException {
        if (checkDayTime(schStartDate, schEndDate, scheduleEndTime, runDayWeek)) {

            boolean scheduleExistCheck = update && updateTrigger(scheduleId, scheduleEndTime, type);

            if (!scheduleExistCheck) {
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put(DATA_MAP_KEY.getCode(), type);

                if (type.equals(LED_PLAY_LIST_END.getValue())) {
                    jobDataMap.put(PRESET_ID, presetId);
                }

                setSchedule(scheduleId, scheduleEndTime, jobDataMap);
            }
        }
    }

    /**
     * * 같은 날, 이후의 시간인지 체크
     */
    private boolean checkDayTime(String scheduleStartDate, String scheduleEndDate, List<String> scheduleTime, String runDayWeek) {
        if (scheduleStartDate.length() == 8 && scheduleEndDate.length() == 8 && scheduleTime.size() == 3) {

            Set<String> daysOfWeek = new HashSet<>(runDayWeek == null ? Arrays.asList("0", "1", "2", "3", "4", "5", "6") : Arrays.asList(runDayWeek.split(",")));

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
     * * playList 영상 재생 시작 날짜 체크
     */
    private ScheduleStatus checkLedPlayStartTime(String scheduleStartDate, String scheduleEndDate, List<String> scheduleStartTime, List<String> scheduleEndTime, String runDayWeek) {
        if (scheduleStartDate.length() == 8 && scheduleEndDate.length() == 8 && scheduleStartTime.size() == 3 && scheduleEndTime.size() == 3) {

            Set<String> daysOfWeek = new HashSet<>(runDayWeek == null ? Arrays.asList("0", "1", "2", "3", "4", "5", "6") : Arrays.asList(runDayWeek.split(",")));

            // 날짜 계산
            LocalDate startDate = LocalDate.of(Integer.parseInt(scheduleStartDate.substring(0, 4)),
                    Integer.parseInt(scheduleStartDate.substring(4, 6)), Integer.parseInt(scheduleStartDate.substring(6))),

                    endDate = LocalDate.of(Integer.parseInt(scheduleEndDate.substring(0, 4)),
                            Integer.parseInt(scheduleEndDate.substring(4, 6)), Integer.parseInt(scheduleEndDate.substring(6))),

                    nowDate = LocalDate.now();

            String dayOfWeek = nowDate.getDayOfWeek().getValue() == 7 ? "0" : String.valueOf(nowDate.getDayOfWeek().getValue());


            // 시작날짜 ~ 종료 날짜가 포함되고 실행 요일이 맞을 경우
            if (daysOfWeek.contains(dayOfWeek) && (startDate.equals(nowDate) || startDate.isBefore(nowDate))
                    && (endDate.equals(nowDate) || endDate.isAfter(nowDate))) {

                // 시간 계산
                LocalTime scheduleStartLocalTime = LocalTime.of(Integer.parseInt(scheduleStartTime.get(0)),
                        Integer.parseInt(scheduleStartTime.get(1)), Integer.parseInt(scheduleStartTime.get(2)));

                LocalTime scheduleEndLocalTime = LocalTime.of(Integer.parseInt(scheduleEndTime.get(0)),
                        Integer.parseInt(scheduleEndTime.get(1)), Integer.parseInt(scheduleEndTime.get(2)));

                LocalTime nowTime = LocalTime.now();

                // 시작 시간 ~ 종료 시간 사이일 경우 스케줄 없이 바로 호출
                if ((scheduleStartLocalTime.isBefore(nowTime) || scheduleStartLocalTime.equals(nowTime)) && scheduleEndLocalTime.isAfter(nowTime)) {
                    return ScheduleStatus.CALL_WITHOUT_SCHEDULE;
                } else if (scheduleStartLocalTime.isAfter(nowTime)) { // 시작 시간 전일 경우 스케줄 등록
                    return ScheduleStatus.REGISTER_SCHEDULE;
                }
            }
        }

        // 코드 값(0 : 아무 동작 없음 / 1 : 스케줄 없이 바로 로직 호출 / 2 : 스케줄 등록)
        return ScheduleStatus.NO_SCHEDULE;
    }

    /**
     * * 스케줄 job, trigger 설정
     */
    private void setSchedule(Long scheduleId, List<String> scheduleTime, JobDataMap jobDataMap) throws SchedulerException {

        JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class)
                .withIdentity(jobDataMap.get(DATA_MAP_KEY.getCode()) + String.valueOf(scheduleId))
                .usingJobData(jobDataMap)
                .requestRecovery(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDataMap.get(DATA_MAP_KEY.getCode()) + String.valueOf(scheduleId))
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(cronExpression(scheduleTime)))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

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

    public Map<String, Object> setPresetRunMap(String type, Object presetId, JobDataMap jobDataMap) {
        Map<String, Object> returnMap = new HashMap<>();
        List<Map<String, Object>> layerInfoList = new ArrayList<>();

        String strPresetId = Long.toString((Long) presetId);

        PresetVo presetVo = presetMapper.getPreset(strPresetId);

        List<LayerVo> layerVoList = presetMapper.getPresetLayers(strPresetId);
        List<PlaylistVo> playlistVoList = playlistMapper.getPlayListByPresetId(strPresetId);

        Map<Integer, Integer> playListIdMap = playlistVoList.stream()
                .collect(Collectors.toMap(
                        PlaylistVo::getLayerId, // layerId로 키 값
                        PlaylistVo::getPlaylistId // playListId로 value
                ));

        for (LayerVo layerVo : layerVoList) {
            Map<String, Object> resultLayerInfo = new HashMap<>();

            Object layerId = layerVo.getLayerId();

            resultLayerInfo.put("layerId", layerId);
            resultLayerInfo.put("playlistId", playListIdMap.get(Integer.parseInt(String.valueOf(layerId))));

            // layer id가 같고 playListId가 다르면 updateYn true
            if (layerId.equals(Integer.parseInt(String.valueOf(jobDataMap.get(LAYER_ID))))
                    && !playListIdMap.get(Integer.parseInt(String.valueOf(layerId))).equals(Integer.parseInt(String.valueOf(jobDataMap.get(PLAYLIST_ID))))) {
                resultLayerInfo.put("updateYn", true);
            } else {
                resultLayerInfo.put("updateYn", false);
            }

            layerInfoList.add(resultLayerInfo);
        }

        returnMap.put("screenId", presetVo.getScreenId());
        returnMap.put("presetId", presetId);
        returnMap.put("layerInfoList", layerInfoList);

        returnMap.put("controlType", type);

        return returnMap;
    }

    public Map<String, Object> presetStatusMap(Object presetId, String targetStatus) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("presetId", presetId);
        queryMap.put("controlType", targetStatus);

        return queryMap;
    }
}

