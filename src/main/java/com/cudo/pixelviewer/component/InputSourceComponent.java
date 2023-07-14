package com.cudo.pixelviewer.component;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.util.ResponseCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InputSourceComponent implements InitializingBean {

    private Map<String, String> inputSourceMap;

    @Override
    public void afterPropertiesSet() {
        inputSourceMap = new HashMap<>();

        inputSourceMap.put("HDMI1", "0x10");
        inputSourceMap.put("HDMI2", "0x11");
        inputSourceMap.put("HDMI3", "0x12");
        inputSourceMap.put("HDMI4", "0x13");
        inputSourceMap.put("HDMI5", "0x14");
        inputSourceMap.put("HDMI6", "0x15");
        inputSourceMap.put("HDMI7", "0x16");
        inputSourceMap.put("DP1", "0x30");
        inputSourceMap.put("DP2", "0x31");
        inputSourceMap.put("DP3", "0x32");
        inputSourceMap.put("DP4", "0x33");
        inputSourceMap.put("DP5", "0x34");
        inputSourceMap.put("DP6", "0x35");
        inputSourceMap.put("DP7", "0x36");
        inputSourceMap.put("DVI1", "0x01");
        inputSourceMap.put("DVI2", "0x02");
        inputSourceMap.put("DVI3", "0x03");
        inputSourceMap.put("DVI4", "0x04");
        inputSourceMap.put("DVI5", "0x05");
        inputSourceMap.put("DVI6", "0x06");
        inputSourceMap.put("DVI7", "0x07");
        inputSourceMap.put("DVI8", "0x08");
        inputSourceMap.put("DVI9", "0x09");
        inputSourceMap.put("DVI10", "0x0A");
        inputSourceMap.put("DVI11", "0x0B");
        inputSourceMap.put("DVI12", "0x0C");
        inputSourceMap.put("DVI13", "0x90");
        inputSourceMap.put("DVI14", "0x91");
        inputSourceMap.put("DVI15", "0x92");
        inputSourceMap.put("DVI16", "0x93");
        inputSourceMap.put("SDI1", "0x20");
        inputSourceMap.put("SDI2", "0x21");
        inputSourceMap.put("SDI3", "0x22");
        inputSourceMap.put("SDI4", "0x23");
        inputSourceMap.put("SDI5", "0x24");
        inputSourceMap.put("SDI6", "0x25");
        inputSourceMap.put("SDI7", "0x26");
        inputSourceMap.put("SDI8", "0x27");
        inputSourceMap.put("SDI9", "0x28");
        inputSourceMap.put("SDI10", "0x29");
        inputSourceMap.put("SDI11", "0x2A");
        inputSourceMap.put("SDI12", "0x2B");
        inputSourceMap.put("SDI13", "0x2C");
        inputSourceMap.put("SDI14", "0x2D");
        inputSourceMap.put("SDI15", "0x2E");
        inputSourceMap.put("SDI16", "0x2F");
        inputSourceMap.put("VGA", "0x40");
        inputSourceMap.put("AV1", "0x41");
        inputSourceMap.put("AV2", "0x42");
    }

    public Map<String, String> getInputSourceMap() {
        return inputSourceMap;
    }

    public int getInputSourceCode(String inputSource) throws ParamException {
        if (inputSourceMap.containsKey(inputSource)) {
            return Integer.decode(inputSourceMap.get(inputSource));
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, "inputSource");
        }
    }

    public String getInputSourceValue(String hexInputSource) {
        String inputSourceCode = "0x" + hexInputSource;

        Optional<String> key = inputSourceMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(inputSourceCode))
                .map(Map.Entry::getKey)
                .findFirst();

        return key.orElse("HDMI1");

    }
}
