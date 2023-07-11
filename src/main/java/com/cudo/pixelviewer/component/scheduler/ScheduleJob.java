package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.component.LedControllerClient;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;
import static com.cudo.pixelviewer.util.TcpClientUtil.floatToHex;
import static com.cudo.pixelviewer.util.TcpClientUtil.getLightByte;

@RequiredArgsConstructor
public class ScheduleJob implements Job {
    final LedControllerClient ledControllerClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(POWER_ON.getValue())) { // 전원 ON
            byte[] message = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x01, (byte) 0x4E, 0x03};

            ledControllerClient.sendMessage(message);

        } else if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(POWER_OFF.getValue())) { // 전원 OFF
            byte[] message = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x00, (byte) 0x4F, 0x03};

            ledControllerClient.sendMessage(message);
        } else if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(LIGHT.getValue())) { // 밝기 조절
            String light = floatToHex((Float) jobDataMap.get(LIGHT.getValue()));

            byte[] lightMessage = getLightByte(light);

            ledControllerClient.sendMessage(lightMessage);

        } else if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(LED_PLAY_LIST.getValue())) {
            System.out.println("밝기 조절");
        }
    }
}
