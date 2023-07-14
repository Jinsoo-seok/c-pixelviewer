package com.cudo.pixelviewer.util;

import java.util.Arrays;

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
        int intValue = Integer.parseInt(hexString, 16);

        return Float.intBitsToFloat(intValue);
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
}
