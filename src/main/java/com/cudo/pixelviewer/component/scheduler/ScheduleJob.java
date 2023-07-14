package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;
import static com.cudo.pixelviewer.util.TcpClientUtil.floatToHex;
import static com.cudo.pixelviewer.util.TcpClientUtil.getLightByte;

@Slf4j
@RequiredArgsConstructor
public class ScheduleJob implements Job {
    final LedControllerClient ledControllerClient;

    final PresetMapper presetMapper;

    final SchedulerFactoryBean schedulerFactoryBean;

    final static String CONTROL_TYPE = "controlType";

    final static String PRESET_ID = "presetId";


    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        if (jobDataMap != null) {
            Object type = jobDataMap.get(DATA_MAP_KEY.getCode());

            if (POWER_ON.getValue().equals(type)) { // 전원 ON
                byte[] powerOnMessage = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x01, (byte) 0x4E, 0x03};
                ledControllerClient.sendMessage(powerOnMessage);

            } else if (POWER_OFF.getValue().equals(type)) { // 전원 OFF
                byte[] powerOffMessage = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x00, (byte) 0x4F, 0x03};
                ledControllerClient.sendMessage(powerOffMessage);


            } else if (LIGHT.getValue().equals(type)) { // 밝기 조절
                String light = floatToHex((Float) jobDataMap.get(LIGHT.getValue()));
                byte[] lightMessage = getLightByte(light);
                ledControllerClient.sendMessage(lightMessage);


            } else if (LED_PLAY_LIST_START.getValue().equals(type) || LED_PLAY_LIST_END.getValue().equals(type)) { // LED 영상 재생 시작/종료
                Map<String, Object> param = new HashMap<>();

                param.put(PRESET_ID, jobDataMap.get(PRESET_ID));

                if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(LED_PLAY_LIST_START.getValue())) {
                    param.put(CONTROL_TYPE, "start");
                    presetMapper.patchPresetStatusRunClear(); // 프리셋 start 상태 클리어
                } else {
                    param.put(CONTROL_TYPE, "stop");
                }

                presetMapper.patchPresetStatusSet(param); // 프리셋 상태 값 업데이트
            }

            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            if (scheduler.checkExists(context.getJobDetail().getKey())) {
                scheduler.deleteJob(context.getJobDetail().getKey());
                log.info("Success delete of terminated schedule. >> {}", context.getJobDetail().getKey());
            }
        }
    }
}
