package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedStatusVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.cudo.pixelviewer.util.TcpClientUtil.*;

@Service
@RequiredArgsConstructor
public class LedServiceImpl implements LedService {
    final LedControllerClient ledControllerClient;

    @Override
    public Map<String, Object> setBrightness(float brightness) {
        Map<String, Object> responseMap = new HashMap<>();

        String light = floatToHex(brightness);

        byte[] lightMessage = getLightByte(light);

        try {
//            ledControllerClient.sendMessage(lightMessage);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> setInputSource(String source) throws ParamException {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // TODO 레이어 번호 어떻게 설정하는지 확인 필요
            byte[] message = {0x33, 0x00, 0x12, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};

            int hexValue = getInputSourceCode(source);

            message[17] = (byte) hexValue;

//            ledControllerClient.sendMessage(message);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> loadPreset(String presetNumber) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            byte[] message = {0x74, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            int presetValue = Integer.decode(String.format("%02x",Integer.parseInt(presetNumber) - 1));
            message[16] = (byte) presetValue;

//            ledControllerClient.sendMessage(message);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> getLedStatus() {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        LedStatusVo ledStatusVo = LedStatusVo.builder()
                .inputSource("HDMI1")
                .powerState(1)
                .brightness(0.51)
                .presetNumber(3)
                .build();

        resultMap.put("data", ledStatusVo);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> getLedPreset() {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩
        List<Map<String, Object>> ledPresetList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> preset = new HashMap<>();

            preset.put("presetId", "0" + Integer.toHexString(i));
            preset.put("presetName", "프리셋" + (i + 1));

            ledPresetList.add(preset);
        }


        resultMap.put("data", ledPresetList);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }
}
