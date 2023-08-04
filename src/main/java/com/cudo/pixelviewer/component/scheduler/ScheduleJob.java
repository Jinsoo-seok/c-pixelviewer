package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.component.DeviceControllerClient;
import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.operate.service.PresetService;
import com.cudo.pixelviewer.vo.LayerVo;
import com.cudo.pixelviewer.vo.PlaylistVo;
import com.cudo.pixelviewer.vo.PresetStatusRunVo;
import com.cudo.pixelviewer.vo.PresetVo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;
import static com.cudo.pixelviewer.util.TcpClientUtil.floatToHex;
import static com.cudo.pixelviewer.util.TcpClientUtil.getLightByte;

@Slf4j
@RequiredArgsConstructor
public class ScheduleJob implements Job {
    final LedControllerClient ledControllerClient;

    final DeviceControllerClient deviceControllerClient;

    final PresetMapper presetMapper;

    final PresetService presetService;

    final SchedulerManager schedulerManager;

    final SchedulerFactoryBean schedulerFactoryBean;

    final static String CONTROL_TYPE = "controlType";

    final static String PRESET_ID = "presetId";

    final static String LAYER_ID = "layerId";

    final static String PLAYLIST_ID = "playListId";


    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        if (jobDataMap != null) {
            Object type = jobDataMap.get(DATA_MAP_KEY.getCode());

            if (POWER_ON.getValue().equals(type)) { // 전원 ON
                byte[] powerOnMessage = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x01, (byte) 0x4E, 0x03};
                deviceControllerClient.sendMessage(powerOnMessage);

            } else if (POWER_OFF.getValue().equals(type)) { // 전원 OFF
                byte[] powerOffMessage = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x00, (byte) 0x4F, 0x03};
                deviceControllerClient.sendMessage(powerOffMessage);


            } else if (LIGHT.getValue().equals(type)) { // 밝기 조절
                String light = floatToHex((Float) jobDataMap.get(LIGHT.getValue()));
                byte[] lightMessage = getLightByte(light);
                ledControllerClient.sendMessage(lightMessage);


            } else if (LED_PLAY_LIST_START.getValue().equals(type) || LED_PLAY_LIST_END.getValue().equals(type)) { // LED 영상 재생 시작/종료
                Map<String, Object> param = new HashMap<>();
                Map<String, Object> presetRunMap = new HashMap<>();

                PresetStatusRunVo usingPresetVo = presetMapper.getUsingPreset();
                param.put(PRESET_ID, jobDataMap.get(PRESET_ID));

                // LED 영상 재생 시작
                if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(LED_PLAY_LIST_START.getValue())) {

                    // 재생중인 프리셋 영상 재생
                    if (usingPresetVo.getPresetId().equals(Integer.parseInt(String.valueOf(jobDataMap.get(PRESET_ID))))) {
                        presetRunMap = schedulerManager.setPresetRunMap("apply", jobDataMap.get(PRESET_ID), jobDataMap);
                    } else { // 재생 중이 아닌 프리셋 영상 재생
                        presetRunMap = schedulerManager.setPresetRunMap("play", jobDataMap.get(PRESET_ID), jobDataMap);
                    }

                    presetService.patchPresetRun(presetRunMap);
                    presetMapper.patchPresetStatusSet(presetStatusMap(jobDataMap.get(PRESET_ID), "play"));
                } else {// LED 영상 재생 종료
                    if (usingPresetVo.getPresetId().equals(Integer.parseInt(String.valueOf(jobDataMap.get(PRESET_ID))))) {
                        param.put(CONTROL_TYPE, "stop");
                        presetMapper.patchPresetStatusSet(param); // 프리셋 상태 값 업데이트
                    }

                }
            }

            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            if (scheduler.checkExists(context.getJobDetail().getKey())) {
                scheduler.deleteJob(context.getJobDetail().getKey());
                log.info("Success delete of terminated schedule. >> {}", context.getJobDetail().getKey());
            }
        }

    }

    public Map<String, Object> presetStatusMap(Object presetId, String targetStatus) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("presetId", presetId);
        queryMap.put("controlType", targetStatus);

        return queryMap;
    }
}
