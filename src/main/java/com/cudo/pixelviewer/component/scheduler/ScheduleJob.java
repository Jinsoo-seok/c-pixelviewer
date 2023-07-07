package com.cudo.pixelviewer.component.scheduler;

import com.cudo.pixelviewer.component.LedControllerClient;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Arrays;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;

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

            if (light.length() == 10) {
                byte[] message = {0x21, 0x00, 0x14, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

                byte[] lightMessage = Arrays.copyOf(message, message.length + 4);

                for (int i = 4; i >= 1; i--) {
                    int hexValue = Integer.decode("0x" + light.substring(i * 2, (i + 1) * 2));
                    lightMessage[lightMessage.length - i] = (byte) hexValue;
                }

            ledControllerClient.sendMessage(lightMessage);
            }
        } else if (jobDataMap.get(DATA_MAP_KEY.getCode()).equals(LED_PLAY.getValue())) {
            System.out.println("밝기 조절");
        }
    }

    public static String floatToHex(float number) {
        int floatToIntBits = Float.floatToIntBits(number);
        String binaryString = Integer.toBinaryString(floatToIntBits);
        String paddedBinaryString = String.format("%32s", binaryString).replace(' ', '0');

        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < paddedBinaryString.length(); i += 4) {
            String fourBits = paddedBinaryString.substring(i, i + 4);
            int decimalValue = Integer.parseInt(fourBits, 2);
            String hexDigit = Integer.toHexString(decimalValue);
            hexString.append(hexDigit);
        }

        return "0x" + hexString.toString().toUpperCase();
    }
}
