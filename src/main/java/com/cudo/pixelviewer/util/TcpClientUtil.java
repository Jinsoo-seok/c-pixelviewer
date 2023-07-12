package com.cudo.pixelviewer.util;

import com.cudo.pixelviewer.config.ParamException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TcpClientUtil {
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

    public static float hexToFloat(String hexString) {
        int binaryValue = Integer.parseInt(hexString, 16);
        String binaryString = Integer.toBinaryString(binaryValue);
        binaryString = String.format("%32s", binaryString).replace(' ', '0');

        int signBit = binaryString.charAt(0) - '0';
        int exponent = Integer.parseInt(binaryString.substring(1, 9), 2) - 127;
        float fraction = 1.0f;

        for (int i = 9, j = 0; i < 32; i++, j++) {
            int bit = binaryString.charAt(i) - '0';
            fraction += bit * Math.pow(2, -j);
        }

        float result = (float) (Math.pow(-1, signBit) * fraction * Math.pow(2, exponent));

        return result;
    }


    public static byte[] getLightByte(String light) {
        byte[] lightMessage = {};

        if (light.length() == 10) {
            byte[] message = {0x21, 0x00, 0x14, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            lightMessage = Arrays.copyOf(message, message.length + 4);

            for (int i = 4; i >= 1; i--) {
                int hexValue = Integer.decode("0x" + light.substring(i * 2, (i + 1) * 2));
                lightMessage[lightMessage.length - i] = (byte) hexValue;
            }
        }
        return lightMessage;
    }

    public static int getInputSourceCode(String inputSource) throws ParamException {
        Map<String, String> map = new HashMap<>();

        map.put("HDMI1", "0x10");
        map.put("HDMI2", "0x11");
        map.put("HDMI3", "0x12");
        map.put("HDMI4", "0x13");
        map.put("HDMI5", "0x14");
        map.put("HDMI6", "0x15");
        map.put("HDMI7", "0x16");
        map.put("DP1", "0x30");
        map.put("DP2", "0x31");
        map.put("DP3", "0x32");
        map.put("DP4", "0x33");
        map.put("DP5", "0x34");
        map.put("DP6", "0x35");
        map.put("DP7", "0x36");
        map.put("DVI1", "0x01");
        map.put("DVI2", "0x02");
        map.put("DVI3", "0x03");
        map.put("DVI4", "0x04");
        map.put("DVI5", "0x05");
        map.put("DVI6", "0x06");
        map.put("DVI7", "0x07");
        map.put("DVI8", "0x08");
        map.put("DVI9", "0x09");
        map.put("DVI10", "0x0A");
        map.put("DVI11", "0x0B");
        map.put("DVI12", "0x0C");
        map.put("DVI13", "0x90");
        map.put("DVI14", "0x91");
        map.put("DVI15", "0x92");
        map.put("DVI16", "0x93");
        map.put("SDI1", "0x20");
        map.put("SDI2", "0x21");
        map.put("SDI3", "0x22");
        map.put("SDI4", "0x23");
        map.put("SDI5", "0x24");
        map.put("SDI6", "0x25");
        map.put("SDI7", "0x26");
        map.put("SDI8", "0x27");
        map.put("SDI9", "0x28");
        map.put("SDI10", "0x29");
        map.put("SDI11", "0x2A");
        map.put("SDI12", "0x2B");
        map.put("SDI13", "0x2C");
        map.put("SDI14", "0x2D");
        map.put("SDI15", "0x2E");
        map.put("SDI16", "0x2F");
        map.put("VGA", "0x40");
        map.put("AV1", "0x41");
        map.put("AV2", "0x42");

        if (map.containsKey(inputSource)) {
            return Integer.decode(map.get(inputSource));
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, "inputSource");
        }
    }
}
