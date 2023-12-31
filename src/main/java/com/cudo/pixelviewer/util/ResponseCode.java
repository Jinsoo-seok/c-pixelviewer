package com.cudo.pixelviewer.util;

public enum ResponseCode {

    SUCCESS(200, "SUCCESS", "SUCCESS"),
    NO_DATA(201, "NO_DATA", "NO_DATA"),
    NO_CONTENT(204, "NO_CONTENT", "No Content"),
    FAIL(500, "FAIL", "FAIL"),
    FAIL_CONNECT(8888, "Fail Connect Server", "Fail Connect Server"),



    // API : 1000
    NO_REQUIRED_PARAM(1000, "NoRequiredParam", "No Required Parameter"),
    NO_REQUIRED_VALUE(1001, "NoRequiredValue", "No Required Parameter Value"),
    INVALID_PARAM_TYPE(1002, "InvalidParamType", "Invalid Parameter Type"),
    INVALID_PARAM_LENGTH(1003, "InvalidParamLength", "Invalid Parameter Length"),
    INVALID_PARAM_VALUE(1004, "InvalidParamValue", "Invalid Parameter Value"),



    // Login, SignUp : 2000
    FAIL_INVALID_USER_ID(2000, "InvalidUserId", "Invalid User ID"),
    FAIL_INVALID_USER_PASSWORD(2001, "InvalidUserPassword", "Invalid User Password"),
    DUPLICATE_ID(2002, "DuplicateId", "Duplicate ID"),
    NOT_EXIST_ID(2003, "NotExistId", "Not Exist ID"),
    FAIL_CHANGE_PW(2004, "FailedChangePW", "Failed Change PW"),
    FAIL_SIGN(2005, "FailedSignUp", "Failed Sign-Up"),


    // Operate : 3000
    // Setting : 3000
    FAIL_UPDATE_SETTING_IMAGE_DEFAULT_PLAYTIME(3000, "FailUpdateSettingImageDefaultPlaytime", "Fail Update Setting ImageDefaultPlaytime"),

    // Screen : 3100
    FAIL_INSERT_SCREEN(3100, "FailedInsertScreen", "Failed Insert Screen"),
    FAIL_UPDATE_SCREEN(3101, "FailedUpdateScreen", "Failed Update Screen"),
    FAIL_DUPLICATE_SCREEN(3102, "FailedDuplicateScreen", "Failed Duplicate Screen"),
    FAIL_DELETE_SCREEN(3103, "FailedDeleteScreen", "Failed Delete Screen"),
    FAIL_NOT_EXIST_SCREEN(3104, "FailedNotExistScreen", "Failed Not Exist Screen"),
    FAIL_DELETE_SCREEN_ALLOCATE_DISPLAY(3105, "FailedDeleteScreenAllocateDisplays", "Failed Delete Screen Allocate Displays"),
    FAIL_INSERT_SCREEN_ALLOCATE_DISPLAYS(3106, "FailedInsertScreenAllocateDisplays", "Failed Insert Screen Allocate Displays"),

    // Display : 3200
    FAIL_UPDATE_DISPLAY(3200, "FailedUpdateDisplay", "Failed Update Display"),
    FAIL_NOT_EXIST_DISPLAY(3201, "FailedNotExistDisplay", "Failed Not Exist Display"),

    // Preset : 3300
    FAIL_INSERT_PRESET(3300, "FailedInsertPreset", "Failed Insert Preset"),
    FAIL_UPDATE_PRESET(3301, "FailedUpdatePreset", "Failed Update Preset"),
    FAIL_DUPLICATE_PRESET(3302, "FailedDuplicatePreset", "Failed Duplicate Preset"),
    FAIL_DELETE_PRESET(3303, "FailedDeletePreset", "Failed Delete Preset"),
    FAIL_NOT_EXIST_PRESET(3304, "FailedNotExistPreset", "Failed Not Exist Preset"),
    FAIL_DELETE_PRESET_ALLOCATE_LAYERS(3305, "FailedDeletePresetAllocateLayers", "Failed Delete Preset Allocate Layers"),
    FAIL_INSERT_PRESET_ALLOCATE_LAYERS(3306, "FailedInsertPresetAllocateLayers", "Failed Insert Preset Allocate Layers"),
    FAIL_UPDATE_PRESET_STATUS(3307, "FailedUpdatePresetStatus", "Failed Update Preset Status"),
    FAIL_UNSUPPORTED_PRESET_STATUS(3308, "FailedUnsupportedPresetStatus", "Failed Unsupported Preset Status"),
    FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE(3309, "FailedUnsupportedPresetControlType", "Failed Unsupported Preset Control Type"),
    FAIL_NOT_EXIST_PRESET_ALLOCATE_RUN_LAYERS(3310, "FailedNotExistPresetAllocateRunLayers", "Failed Not Exist Preset Allocate Run Layers"),

    FAIL_UPDATE_NOT_EXIST_PLAYLIST(3311, "FailedUpdateNotExistPlaylist", "Failed Update Not Exist Playlist"),
    ALREADY_PLAYING_PRESET(3312, "AlreadyPlayingPreset", "Already Playing Preset"),

    FAIL_PRESET_RUN_TO_AGENT(3314, "FailedPresetRunToAgent", "Failed Preset Run To Agent"),
    FAIL_AGENT_TO_VIEWER(3315, "FailedAgentToViewer", "Failed Agent To Viewer"),


    // Layer : 3400
    FAIL_INSERT_LAYER(3400, "FailedInsertLayer", "Failed Insert Layer"),
    FAIL_UPDATE_LAYER(3401, "FailedUpdateLayer", "Failed Update Layer"),
    FAIL_DUPLICATE_LAYER(3402, "FailedDuplicateLayer", "Failed Duplicate Layer"),
    FAIL_DELETE_LAYER(3403, "FailedDeleteLayer", "Failed Delete Layer"),
    FAIL_NOT_EXIST_LAYER(3404, "FailedNotExistLayer", "Failed Not Exist Layer"),

    // Playlist : 3500
    FAIL_INSERT_PLAYLIST(3500, "FailedInsertPlaylist", "Failed Insert Playlist"),
    FAIL_UPDATE_PLAYLIST(3501, "FailedUpdatePlaylist", "Failed Update Playlist"),
    FAIL_DUPLICATE_PLAYLIST(3502, "FailedDuplicatePlaylist", "Failed Duplicate Playlist"),
    FAIL_DELETE_PLAYLIST(3503, "FailedDeletePlaylist", "Failed Delete Playlist"),
    FAIL_NOT_EXIST_PLAYLIST(3504, "FailedNotExistPlaylist", "Failed Not Exist Playlist"),

    FAIL_INSERT_PLAYLIST_CONTENTS(3510, "FailedInsertPlaylistContents", "Failed Insert PlaylistContents"),
    FAIL_UPDATE_PLAYLIST_CONTENTS(3511, "FailedUpdatePlaylistContents", "Failed Update PlaylistContents"),
    FAIL_DUPLICATE_PLAYLIST_CONTENTS(3512, "FailedDuplicatePlaylistContents", "Failed Duplicate PlaylistContents"),
    FAIL_DELETE_PLAYLIST_CONTENTS(3513, "FailedDeletePlaylistContents", "Failed Delete PlaylistContents"),
    FAIL_NOT_EXIST_PLAYLIST_CONTENTS(3514, "FailedNotExistPlaylistContents", "Failed Not Exist PlaylistContents"),

    // External : 3600
    FAIL_INSERT_EXTERNAL_VIDEO(3600, "FailedInsertExternalVideo", "Failed Insert ExternalVideo"),
    FAIL_UPDATE_EXTERNAL_VIDEO(3601, "FailedUpdateExternalVideo", "Failed Update ExternalVideo"),
    FAIL_DUPLICATE_EXTERNAL_VIDEO(3602, "FailedDuplicateExternalVideo", "Failed Duplicate ExternalVideo"),
    FAIL_UNSUPPORTED_TYPE_EXTERNAL_VIDEO(3603, "FailedUnsupportedTypeExternalVideo", "Failed Unsupported Type ExternalVideo"),

    FAIL_INSERT_EXTERNAL_INFO(3603, "FailedInsertExternalInfo", "Failed Insert ExternalInfo"),
    FAIL_UPDATE_EXTERNAL_INFO(3604, "FailedUpdateExternalInfo", "Failed Update ExternalInfo"),
    FAIL_DUPLICATE_EXTERNAL_INFO(3605, "FailedDuplicateExternalInfo", "Failed Duplicate ExternalInfo"),
    FAIL_UNSUPPORTED_TYPE_EXTERNAL_INFO(3606, "FailedUnsupportedTypeExternalInfo", "Failed Unsupported Type ExternalInfo"),

    // Subtitle : 3700
    FAIL_INSERT_SUBTITLE(3700, "FailedInsertSubtitle", "Failed Insert Subtitle"),
    FAIL_UPDATE_SUBTITLE(3701, "FailedUpdateSubtitle", "Failed Update Subtitle"),
    FAIL_DUPLICATE_SUBTITLE(3702, "FailedDuplicateSubtitle", "Failed Duplicate Subtitle"),
    FAIL_NOT_EXIST_SUBTITLE(3703, "FailedNotExistSubtitle", "Failed Not Exist Subtitle"),
    FAIL_UNSUPPORTED_TYPE_SUBTITLE(3704, "FailedUnsupportedTypeSubtitle", "Failed Unsupported Type Subtitle"),
    FAIL_UPDATE_SUBTITLE_LAYER(3705, "FailedUpdateSubtitleLayer", "Failed Unsupported Type Subtitle Layer"),

    // BO : 4000
    // Pwrcon : 4100
    FAIL_INSERT_PWRCON(4100, "FailedInsertPwrcon", "Failed Insert Pwrcon"),
    FAIL_UPDATE_PWRCON(4101, "FailedUpdatePwrcon", "Failed Update Pwrcon"),
    FAIL_DUPLICATE_PWRCON(4102, "FailedDuplicatePwrcon", "Failed Duplicate Pwrcon"),
    FAIL_DELETE_PWRCON(4103, "FailedDeletePwrcon", "Failed Delete Pwrcon"),
    FAIL_NOT_EXIST_PWRCON(4104, "FailedNotExistPwrcon", "Failed Not Exist Pwrcon"),

    // LedCon : 4200
    FAIL_INSERT_LEDCON(4200, "FailedInsertLedcon", "Failed Insert Ledcon"),
    FAIL_UPDATE_LEDCON(4201, "FailedUpdateLedcon", "Failed Update Ledcon"),
    FAIL_DUPLICATE_LEDCON(4202, "FailedDuplicateLedcon", "Failed Duplicate Ledcon"),
    FAIL_DELETE_LEDCON(4203, "FailedDeleteLedcon", "Failed Delete Ledcon"),
    FAIL_NOT_EXIST_LEDCON(4204, "FailedNotExistLedcon", "Failed Not Exist Ledcon"),

    FAIL_DUPLICATE_IP(4205, "FailedDuplicateIp", "Failed Duplicate Ip"),

    // Setting : 4300
    FAIL_UPDATE_SETTING_VALUES(4300, "FailedUpdateSettingValues", "Failed to Update Setting Values"),

    FAIL_UPDATE_SETTING_VIEW_TOP_MOST_EN(4301, "FailedUpdateSettingViewTopMostEn", "Failed to Update Setting View Top Most En"),
    FAIL_UPDATE_SETTING_VIEW_TEMP_HUMI_EN(4302, "FailedUpdateSettingViewTempHumiEn", "Failed to Update Setting View Temp Humi En"),

    FAIL_UPDATE_SETTING_LED_COMM_TYPE(4303, "FailedUpdateSettingLedCommType", "Failed to Update Setting LED Comm Type"),
    FAIL_UPDATE_SETTING_LED_PRESET_EN(4304, "FailedUpdateSettingLedPresetEn", "Failed to Update Setting LED Preset En"),
    FAIL_UPDATE_SETTING_LED_INPUT_SELECT_EN(4305, "FailedUpdateSettingLedInputSelectEn", "Failed to Update Setting LED Input Select En"),
    FAIL_UPDATE_SETTING_LED_BRIGHTNESS_CONTROL_EN(4306, "FailedUpdateSettingLedBrightnessControlEn", "Failed to Update Setting LED Brightness Control En"),
    FAIL_UPDATE_SETTING_LED_PRESET_COUNT(4307, "FailedUpdateSettingLedPresetCount", "Failed to Update Setting LED Preset Count"),

    FAIL_UPDATE_SETTING_PWR_CONTROL_EN(4308, "FailedUpdateSettingPwrControlEn", "Failed to Update Setting Pwr Control En"),

    FAIL_UPDATE_SETTING_LOGIN_EN(4309, "FailedUpdateSettingLoginEn", "Failed to Update Setting Login En"),
    FAIL_UPDATE_SETTING_IMG_DEFAULT_PLAYTIME(4310, "FailedUpdateSettingImgDefaultPlaytime", "Failed to Update Setting Img Default Playtime"),
    FAIL_UPDATE_SETTING_EXTERNALINFO_AREA(4311, "FailedUpdateSettingExternalinfoArea", "Failed to Update Setting Externalinfo Area"),


    FAIL_INSERT_DISPLAY_SETTING(4312, "FailedInsertDisplaySetting", "Failed Insert Display Setting"),
    FAIL_UPDATE_DISPLAY_SETTING(4313, "FailedUpdateDisplaySetting", "Failed Update Display Setting"),
    FAIL_DUPLICATE_DISPLAY_SETTING(4314, "FailedDuplicateDisplaySetting", "Failed Duplicate Display Setting"),
    FAIL_DELETE_DISPLAY_SETTING(4315, "FailedDeleteDisplaySetting", "Failed Delete Display Setting"),
    FAIL_NOT_EXIST_DISPLAY_SETTING(4316, "FailedNotExistDisplaySetting", "Failed Not Exist Display Setting"),
    FAIL_USED_DISPLAY_SETTING(4317, "FailedUsedDisplaySetting", "Failed Used Display Setting"),
    FAIL_DISPLAY_SETTING_TO_AGENT(4318, "FailedDisplaySettingToAgent", "Failed Used Display Setting to Agent"),
    FAIL_UPDATE_ADMIN_SETTING_WEATHER_IMG(4319, "FailedUpdateAdminSettingWeatherImage", "Failed Update AdminSetting Weather Image"),
    FAIL_UPDATE_ADMIN_SETTING_AIR_IMG(4320, "FailedUpdateAdminSettingAirImage", "Failed Update AdminSetting Air Image"),

    FAIL_GET_EXTERNALS_DATA(4321, "FailedGetExternalsData", "Failed Get Externals Data"),
    FAIL_UNSUPPORTED_TYPE_SETTING(4322, "FailedUnsupportedTypeSetting", "Failed Unsupported Type Setting"),

    // Externals : 4400
    FAIL_INSERT_EXTERNALS_AIR(4401, "FailedInsertExternalsAir", "Failed Insert Externals Air"),
    FAIL_EXTERNALS_AIR(4402, "FailedExternalsAir", "Failed Externals Air"),
    FAIL_INSERT_EXTERNALS_WEATHER(4403, "FailedInsertExternalsWeather", "Failed Insert Externals Weather"),
    FAIL_EXTERNALS_WEATHER(4404, "FailedExternalsWeather", "Failed Externals Weather");







    private final int code;
    private final String codeName;
    private final String message;

    ResponseCode(int code, String codeName, String message){
        this.code = code;
        this.codeName = codeName;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getMessage() {
        return message;
    }
}