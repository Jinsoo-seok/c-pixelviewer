package com.cudo.pixelviewer.util;

import com.cudo.pixelviewer.config.ParamException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ParameterUtils {

    public static void parameterValidation(Map<String, Object> param, String[] keyList) throws ParamException {
        for(String key : keyList) {
            boolean result = param.containsKey(key);
            if(!result) {
                throw new ParamException(ResponseCode.NO_REQUIRED_PARAM, key);
            }
        }
    }

    private static void validationRequired(String parameterKey, Object parameterValue) throws ParamException {
        boolean paramTF = (parameterValue == null || String.valueOf(parameterValue).trim().isEmpty());

        if(paramTF) {
            throw new ParamException(ResponseCode.NO_REQUIRED_VALUE, parameterKey);
        }
    }

    public static int parameterInt(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        if (parameterValue instanceof Integer) {
            int result = (int) parameterValue;

            return result;
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }

    }

    public static Double parameterDouble(String parameterKey, Object parameterValue, boolean required, double maxValue) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        if (parameterValue instanceof Double) {
            Double result = (Double) parameterValue;

            if (maxValue > 0 && result > maxValue) {
                throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
            }

            return result;
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }

    }

    public static String parameterString(String parameterKey, Object parameterValue, boolean required, int maxLength, String regExp) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        if(parameterValue instanceof String) {
            String paramResult = String.valueOf(parameterValue);
            int resultSize = paramResult.length();

            if(maxLength > 0) {
                if(resultSize > maxLength) {
                    throw new ParamException(ResponseCode.INVALID_PARAM_LENGTH, parameterKey);
                }
            }

            if (regExp != null) {
                boolean regExpResult = paramResult.matches(regExp);

                if(!regExpResult) {
                    throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
                }
            }

            return paramResult;
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }
    }

    public static String parameterYn(String parameterKey, Object parameterValue, String regExp, boolean required) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        if(parameterValue instanceof String) {
            String paramResult = String.valueOf(parameterValue);
            int resultSize = paramResult.length();

            if(resultSize > 1) {
                throw new ParamException(ResponseCode.INVALID_PARAM_LENGTH, parameterKey);
            }

            boolean regExpResult = paramResult.matches(regExp);

            if(!regExpResult) {
                throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
            }

            return paramResult;
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }
    }

    public static void parameterMap(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if (! (parameterValue instanceof Map)) {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }

        if(required) {
            int paramSize = ((Map<Object, Object>) parameterValue).size();

            if(paramSize == 0) {
                throw new ParamException(ResponseCode.NO_REQUIRED_VALUE, parameterKey);
            }
        }
    }

    public static void parameterArray(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if (! (parameterValue instanceof ArrayList)) {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }

        if(required) {
            int paramSize = ((ArrayList) parameterValue).size();

            if(paramSize == 0) {
                throw new ParamException(ResponseCode.NO_REQUIRED_VALUE, parameterKey);
            }
        }
    }

    public static Boolean parameterBoolean(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if (required) {
            validationRequired(parameterKey, parameterValue);
        }

        if (parameterValue instanceof Boolean) {
            Boolean result = (Boolean) parameterValue;

            return result;
        } else if (parameterValue instanceof String) {
            String stringValue = (String) parameterValue;
            Boolean result = Boolean.parseBoolean(stringValue);

            return result;
        } else {
            throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
        }
    }

    public static void parameterIntCompareToTrue(String parameterKey, int parameterValue1, int parameterValue2, int operator) throws ParamException {
        int result = Integer.compare(parameterValue1, parameterValue2);

        if(operator != result) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
        }
    }

    public static void parameterIntCompareToFalse(String parameterKey, int parameterValue1, int parameterValue2, int operator) throws ParamException {
        int result = Integer.compare(parameterValue1, parameterValue2);

        if(operator == result) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
        }
    }

    public static Map<String, Object> pageOption(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        int pageCount = ((param.containsKey("pageCount") == false) ? 10 : (int) param.get("pageCount"));
        int pageNum = ((param.containsKey("pageNum") == false) ? 1 : (int) param.get("pageNum"));
        int page = ((pageNum-1) * pageCount);

        result.put("pageCount", pageCount);
        result.put("pageNum", pageNum);
        result.put("page", page);

        return result;
    }

    public static Map<String, Object> responseOption(String type) {
        Map<String, Object> result = new HashMap<>();

        if(type.equals("SUCCESS")){
            result.put("code", ResponseCode.SUCCESS.getCode());
            result.put("message", ResponseCode.SUCCESS.getMessage());
        }
        else if(type.equals("NO_CONTENT")){
            result.put("code", ResponseCode.NO_CONTENT.getCode());
            result.put("message", ResponseCode.NO_CONTENT.getMessage());
        }
        else if(type.equals("FAIL")){
            result.put("code", ResponseCode.FAIL.getCode());
            result.put("message", ResponseCode.FAIL.getMessage());
        }
        else{
            result.put("code", ResponseCode.FAIL.getCode());
            result.put("message", ResponseCode.FAIL.getMessage());
        }
        return result;
    }

    public static void parameterPeriod(String parameterKey, int parameterLength, int maxLength) throws ParamException {
        if(parameterLength > maxLength) {
            throw new ParamException(ResponseCode.INVALID_PARAM_LENGTH, parameterKey);
        }
    }

    public static boolean parameterDate(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        try {
            if (parameterValue instanceof String) {
                SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd");
                dateFormatParser.setLenient(false);
                dateFormatParser.parse(String.valueOf(parameterValue)); //대상 값 포맷에 적용되는지 확인

                return true;
            } else {
                throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
            }
        } catch (Exception e) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
        }
    }

    public static boolean parameterTime(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        try {
            if (parameterValue instanceof String) {
                SimpleDateFormat dateFormatParser = new SimpleDateFormat("HHmm");
                dateFormatParser.setLenient(false);
                dateFormatParser.parse(String.valueOf(parameterValue)); //대상 값 포맷에 적용되는지 확인

                return true;
            } else {
                throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
            }
        } catch (Exception e) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
        }
    }

    public static boolean parameterDateTime(String parameterKey, Object parameterValue, boolean required) throws ParamException {
        if(required) {
            validationRequired(parameterKey, parameterValue);
        }

        try {
            if (parameterValue instanceof String) {
                SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMddHHmm");
                dateFormatParser.setLenient(false);
                dateFormatParser.parse(String.valueOf(parameterValue)); //대상 값 포맷에 적용되는지 확인

                return true;
            } else {
                throw new ParamException(ResponseCode.INVALID_PARAM_TYPE, parameterKey);
            }
        } catch (Exception e) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
        }
    }

    public static boolean parameterCompareDate(String startDateParamKey, String endDateParamKey, Object startDate, Object endDate) throws ParamException {

        try {
            SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd");
            return compareDate(startDateParamKey, endDateParamKey, startDate, endDate, dateFormatParser);
        } catch (Exception e) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, startDateParamKey + ", " + endDateParamKey);
        }

    }

    public static boolean parameterCompareDateTime(String startDateParamKey, String endDateParamKey, Object startDate, Object endDate) throws ParamException {

        try {
            SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMddHHmm");
            return compareDate(startDateParamKey, endDateParamKey, startDate, endDate, dateFormatParser);
        } catch (Exception e) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, startDateParamKey + ", " + endDateParamKey);
        }

    }

    private static boolean compareDate(String startDateParamKey, String endDateParamKey, Object startDate, Object endDate, SimpleDateFormat dateFormatParser) throws ParseException, ParamException {
        dateFormatParser.setLenient(false);
        Date start = dateFormatParser.parse(String.valueOf(startDate));
        Date end = dateFormatParser.parse(String.valueOf(endDate));

        int result = start.compareTo(end);

        if (result > 0) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, startDateParamKey + ", " + endDateParamKey);
        }

        return true;
    }

    public static boolean parameterScheduleDay(String parameterKey, Object parameterValue) throws ParamException {
        if (parameterValue != null && ((ArrayList<Integer>)parameterValue).size() > 7) {
            throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, parameterKey);
        }

        return true;
    }
}
