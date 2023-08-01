-- CREATE SCHEMA IF NOT EXISTS pixelviewer DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `Device_Controls` (
    `condevice_id` int(11) NOT NULL AUTO_INCREMENT,
    `ip` varchar(255) NOT NULL,
    `port` int(11) NOT NULL,
    `model_nm` varchar(255) NOT NULL,
    `serial_no` varchar(255) NOT NULL,
    `mpcmu_cnt` int(11) NOT NULL,
    `dpcmu_cnt` int(11) NOT NULL,
    `state` varchar(255) NOT NULL,
    `spec_1` varchar(255) NOT NULL,
    `spec_2` varchar(255) NOT NULL,
    `spec_3` varchar(255) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `chg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '교체일',
    `update_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6) COMMENT '수정일',
    `led_pwr` tinyint(4) NOT NULL,
    `temp` int(11) NOT NULL,
    `humi` int(11) NOT NULL,
    `led_key` varchar(255) NOT NULL,
    PRIMARY KEY (`condevice_id`),
    UNIQUE KEY `UQ_1` (`ip`,`serial_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Displays` (
    `display_id` int(11) NOT NULL,
    `display_nm` varchar(255) NOT NULL,
    `pos_x` int(11) NOT NULL,
    `pos_y` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `promary_fl` tinyint(4) NOT NULL,
    `pattern_fl` tinyint(4) NOT NULL,
    `row` int(11) NOT NULL,
    `column` int(11) NOT NULL,
    PRIMARY KEY (`display_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Displays_Setting` (
    `display_id` int(11) NOT NULL AUTO_INCREMENT,
    `display_nm` varchar(255) NOT NULL,
    `gpu_nm` varchar(255) NOT NULL,
    `pos_x` int(11) NOT NULL,
    `pos_y` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `primary_fl` tinyint(4) NOT NULL,
    PRIMARY KEY (`display_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `ExternalData` (
    `external_id` int(11) NOT NULL AUTO_INCREMENT,
    `external_type` varchar(50) NOT NULL,
    `external_data` text NOT NULL,
    `external_update_date` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `ExternalVideo` (
    `ex_video_id` int(11) NOT NULL AUTO_INCREMENT,
    `screen_id` int(11) NOT NULL,
    `preset_id` int(11) NOT NULL,
    `layer_id` int(11) NOT NULL,
    `object_id` int(11) NOT NULL,
    `type` int(11) NOT NULL,
    `pos_x` int(11) NOT NULL,
    `pos_y` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `ord` int(11) NOT NULL,
    `rtsp_url` varchar(255) NOT NULL,
    `video_nm` int(11) NOT NULL,
    `video_format` varchar(255) NOT NULL,
    `update_date` datetime NOT NULL,
    PRIMARY KEY (`ex_video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `External_Info` (
    `ex_info_id` int(11) NOT NULL AUTO_INCREMENT,
    `screen_id` int(11) NOT NULL,
    `preset_id` int(11) NOT NULL,
    `layer_id` int(11) NOT NULL,
    `object_id` int(11) NOT NULL,
    `type` int(11) NOT NULL,
    `pos_x` int(11) NOT NULL,
    `pos_y` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `ord` int(11) NOT NULL,
    `image_path` varchar(255) NOT NULL DEFAULT '""',
    `fore_color` varchar(255) NOT NULL,
    `font_nm` varchar(255) NOT NULL,
    `font_fl` varchar(255) NOT NULL,
    `font_size` int(11) NOT NULL,
    `font_color` varchar(255) NOT NULL,
    `border_size` int(11) NOT NULL,
    `border_color` varchar(255) NOT NULL,
    `back_color` varchar(255) NOT NULL,
    `update_date` datetime NOT NULL,
    PRIMARY KEY (`ex_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Font_Info` (
    `font_id` int(11) NOT NULL AUTO_INCREMENT,
    `font_nm` varchar(100) NOT NULL,
    `font_fl` varchar(100) NOT NULL,
    PRIMARY KEY (`font_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LED_Controls` (
    `led_controller_id` int(11) NOT NULL AUTO_INCREMENT,
    `ip` varchar(255) NOT NULL,
    `port` int(11) NOT NULL DEFAULT 9999,
    `model_nm` varchar(255) NOT NULL,
    `version` varchar(255) NOT NULL,
    `serial_no` varchar(255) NOT NULL,
    `firmware_ver` varchar(255) DEFAULT NULL,
    `state` int(11) NOT NULL,
    `spec_1` varchar(255) NOT NULL,
    `spec_2` varchar(255) NOT NULL,
    `spec_3` varchar(255) NOT NULL,
    `connect_fl` tinyint(4) NOT NULL,
    `use_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '사용일',
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `chg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '교체일',
    `upd_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6) COMMENT '수정일',
    `videoinput_mode` varchar(255) NOT NULL,
    `input_resolution_width` varchar(255) NOT NULL,
    `input_resolution_height` varchar(255) NOT NULL,
    `frame` int(11) NOT NULL,
    `color_depth` int(11) NOT NULL,
    `color_mode` varchar(255) NOT NULL,
    `hdr_en` tinyint(4) NOT NULL,
    `preset` int(11) NOT NULL,
    `brightness` int(11) NOT NULL,
    `color_temp` int(11) NOT NULL,
    PRIMARY KEY (`led_controller_id`),
    UNIQUE KEY `IDX_c363c7d3fd74a2f003a975e28b` (`ip`,`serial_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LED_Events` (
    `event_id` int(11) NOT NULL,
    `rccard_id` int(11) NOT NULL,
    `led_controller_id` int(11) NOT NULL,
    `led_port` int(11) NOT NULL,
    `daisychain_no` int(11) NOT NULL,
    `state_val` int(11) NOT NULL,
    `temp` int(11) NOT NULL,
    `event_msg` varchar(255) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `rccardIdRccardId` int(11) DEFAULT NULL,
    PRIMARY KEY (`event_id`),
    UNIQUE KEY `IDX_f50c4df169b96bbf48852e2b6a` (`rccard_id`),
    KEY `FK_f1104606efc0804ff7ea9beb421` (`rccardIdRccardId`),
    CONSTRAINT `FK_f1104606efc0804ff7ea9beb421` FOREIGN KEY (`rccardIdRccardId`) REFERENCES `ReceivingCards` (`rccard_id`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LED_Ports` (
    `led_control_ip` int(11) NOT NULL AUTO_INCREMENT,
    `Port_1` int(11) NOT NULL,
    `Port_2` int(11) NOT NULL,
    `Port_3` int(11) NOT NULL,
    `Port_4` int(11) NOT NULL,
    `Port_5` int(11) NOT NULL,
    `Port_6` int(11) NOT NULL,
    `Port_7` int(11) NOT NULL,
    `Port_8` int(11) NOT NULL,
    `Port_9` int(11) NOT NULL,
    `Port_10` int(11) NOT NULL,
    `Port_11` int(11) NOT NULL,
    `Port_12` int(11) NOT NULL,
    `Port_13` int(11) NOT NULL,
    `Port_14` int(11) NOT NULL,
    `Port_15` int(11) NOT NULL,
    `Port_16` int(11) NOT NULL,
    PRIMARY KEY (`led_control_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LED_Preset_Info` (
    `preset_number` varchar(2) NOT NULL COMMENT '프리셋 번호',
    `preset_name` varchar(100) NOT NULL COMMENT '프리셋 명',
    `last_load_time` datetime DEFAULT NULL COMMENT '마지막 실행 체크(0:미실행/1:실행)',
    PRIMARY KEY (`preset_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LED_Realtime` (
    `realtime_id` int(11) NOT NULL,
    `rccard_id` int(11) NOT NULL,
    `led_controller_id` int(11) NOT NULL,
    `led_port` int(11) NOT NULL,
    `daisychain_no` int(11) NOT NULL,
    `state_val` int(11) NOT NULL,
    `temp` int(11) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `rccardIdRccardId` int(11) DEFAULT NULL,
    PRIMARY KEY (`realtime_id`),
    UNIQUE KEY `IDX_dc58ef0b67d2bc09be01bf798b` (`rccard_id`),
    KEY `FK_48bc706a42035700aab68f3d82e` (`rccardIdRccardId`),
    CONSTRAINT `FK_48bc706a42035700aab68f3d82e` FOREIGN KEY (`rccardIdRccardId`) REFERENCES `ReceivingCards` (`rccard_id`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LED_Screens_Info` (
    `led_screen_id` int(11) NOT NULL,
    `led_screen_nm` varchar(255) NOT NULL,
    `pixel_pitch` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `row` int(11) NOT NULL,
    `column` int(11) NOT NULL,
    `horizontal_pixel` int(11) NOT NULL,
    `vertical_pixel` int(11) NOT NULL,
    PRIMARY KEY (`led_screen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `LayerObjects` (
    `object_id` int(11) NOT NULL AUTO_INCREMENT,
    `layer_id` int(11) NOT NULL,
    `preset_id` int(11) NOT NULL,
    `screen_id` int(11) NOT NULL,
    `type` int(11) NOT NULL,
    `object_nm` varchar(255) NOT NULL,
    PRIMARY KEY (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Layers` (
    `layer_id` int(11) NOT NULL AUTO_INCREMENT,
    `preset_id` int(11) NOT NULL,
    `screen_id` int(11) NOT NULL,
    `layer_nm` varchar(255) NOT NULL,
    `pos_x` int(11) NOT NULL,
    `pos_y` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `ord` int(11) NOT NULL,
    `sub_first_en` tinyint(4) NOT NULL,
    `sub_second_en` tinyint(4) NOT NULL,
    `ex_video_en` tinyint(4) NOT NULL,
    `weather_en` tinyint(4) NOT NULL,
    `air_en` tinyint(4) NOT NULL,
    `viewer_status` varchar(45) NOT NULL DEFAULT 'none',
    `viewer_yn` tinyint(4) NOT NULL DEFAULT 0,
    PRIMARY KEY (`layer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `PCMU` (
    `pcmu_id` int(11) NOT NULL AUTO_INCREMENT,
    `current_val` int(11) NOT NULL,
    `relay_state` int(11) NOT NULL,
    `state` int(11) NOT NULL,
    `event_ac` varchar(255) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `chg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '교체일',
    `upd_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6) COMMENT '수정일',
    `led_key` varchar(255) NOT NULL,
    `condevice_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`pcmu_id`),
    KEY `FK_6f7a4b77a4b4b4c7285d2d15145` (`condevice_id`),
    CONSTRAINT `FK_6f7a4b77a4b4b4c7285d2d15145` FOREIGN KEY (`condevice_id`) REFERENCES `Device_Controls` (`condevice_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `PCMU_Events` (
    `pcmu_event_id` int(11) NOT NULL AUTO_INCREMENT,
    `pcmu_id` int(11) NOT NULL,
    `condevice_id` int(11) NOT NULL,
    `current_val` int(11) NOT NULL,
    `current_avg` int(11) NOT NULL,
    `event_msg` varchar(255) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `pcmuEventIdPcmuId` int(11) DEFAULT NULL,
    PRIMARY KEY (`pcmu_event_id`),
    KEY `FK_38fc1db1ef5da7cbe6f7d43db97` (`pcmuEventIdPcmuId`),
    CONSTRAINT `FK_38fc1db1ef5da7cbe6f7d43db97` FOREIGN KEY (`pcmuEventIdPcmuId`) REFERENCES `PCMU` (`pcmu_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `PCMU_Logs` (
    `pcmu_log_id` int(11) NOT NULL AUTO_INCREMENT,
    `pcmu_id` int(11) NOT NULL,
    `condevice_id` int(11) NOT NULL,
    `error_status` int(11) NOT NULL,
    `fix_current_val` int(11) NOT NULL,
    `current_val` int(11) NOT NULL,
    `current_avg` int(11) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '생성일',
    `pcmuLogIdPcmuId` int(11) DEFAULT NULL,
    PRIMARY KEY (`pcmu_log_id`),
    KEY `FK_7a4622de863cfab0eafe83a0c9d` (`pcmuLogIdPcmuId`),
    CONSTRAINT `FK_7a4622de863cfab0eafe83a0c9d` FOREIGN KEY (`pcmuLogIdPcmuId`) REFERENCES `PCMU` (`pcmu_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `PCMU_Realtime` (
    `pcmu_realtime_id` int(11) NOT NULL AUTO_INCREMENT,
    `pcmu_id` int(11) NOT NULL,
    `current_val` int(11) NOT NULL,
    `current_avg` int(11) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `led_key` varchar(255) NOT NULL,
    `pcmuRealtimeIdPcmuId` int(11) DEFAULT NULL,
    PRIMARY KEY (`pcmu_realtime_id`),
    KEY `FK_86a6e7138d56acbf0cbc59622a1` (`pcmuRealtimeIdPcmuId`),
    CONSTRAINT `FK_86a6e7138d56acbf0cbc59622a1` FOREIGN KEY (`pcmuRealtimeIdPcmuId`) REFERENCES `PCMU` (`pcmu_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Playlist_Items` (
    `item_id` int(11) NOT NULL AUTO_INCREMENT,
    `ord_no` int(11) NOT NULL,
    `playlist_id` int(11) NOT NULL,
    `type` int(11) NOT NULL,
    `cts_nm` varchar(255) NOT NULL,
    `cts_path` varchar(255) NOT NULL,
    `playtime` int(50) NOT NULL,
    `weather_fl` tinyint(4) NOT NULL,
    `air_info_fl` tinyint(4) NOT NULL,
    `stretch` tinyint(4) NOT NULL,
    PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Playlists` (
    `playlist_id` int(11) NOT NULL AUTO_INCREMENT,
    `playlist_nm` varchar(255) NOT NULL,
    `layer_id` int(11) NOT NULL,
    `preset_id` int(11) NOT NULL,
    `screen_id` int(11) NOT NULL,
    `content_id_list` text DEFAULT '0',
    `select_yn` varchar(2) NOT NULL DEFAULT 'N',
    `update_date` datetime NOT NULL,
    PRIMARY KEY (`playlist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Presets` (
    `preset_id` int(11) NOT NULL AUTO_INCREMENT,
    `screen_id` int(11) NOT NULL,
    `preset_nm` varchar(255) NOT NULL,
    `rowsize` int(11) NOT NULL,
    `columnsize` int(11) NOT NULL,
    `user_style_yn` tinyint(4) NOT NULL,
    `preset_status` varchar(45) NOT NULL,
    `update_date` datetime DEFAULT current_timestamp(),
    PRIMARY KEY (`preset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `ReceivingCards` (
    `rccard_id` int(11) NOT NULL,
    `led_controller_id` int(11) NOT NULL,
    `led_port` int(11) NOT NULL,
    `daisychain_no` int(11) NOT NULL,
    `model_nm` varchar(255) NOT NULL,
    `version` varchar(255) NOT NULL,
    `cur_val` int(11) NOT NULL,
    `temp` int(11) NOT NULL,
    `width` int(11) NOT NULL,
    `height` int(11) NOT NULL,
    `pos_x` int(11) NOT NULL,
    `pos_y` int(11) NOT NULL,
    `runtime` varchar(255) NOT NULL,
    `state` int(11) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `chg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '교체일',
    `upd_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '수정일',
    `ledControllerIdLedControllerId` int(11) DEFAULT NULL,
    `ledcontrolLedControllerId` int(11) DEFAULT NULL,
    PRIMARY KEY (`rccard_id`),
    KEY `FK_b961df8da508f566e4b69f8e40c` (`ledControllerIdLedControllerId`),
    KEY `FK_93dfda827c73bebb2480cffbb62` (`ledcontrolLedControllerId`),
    CONSTRAINT `FK_93dfda827c73bebb2480cffbb62` FOREIGN KEY (`ledcontrolLedControllerId`) REFERENCES `LED_Controls` (`led_controller_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_b961df8da508f566e4b69f8e40c` FOREIGN KEY (`ledControllerIdLedControllerId`) REFERENCES `LED_Controls` (`led_controller_id`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Screen_Displays` (
     `src_display_id` int(11) NOT NULL AUTO_INCREMENT,
     `screen_id` int(11) NOT NULL,
     `display_id` int(11) NOT NULL,
     `rowsize` int(11) NOT NULL,
     `columnsize` int(11) NOT NULL,
     PRIMARY KEY (`src_display_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Screens` (
    `screen_id` int(11) NOT NULL AUTO_INCREMENT,
    `screen_nm` varchar(255) DEFAULT NULL,
    `rowsize` int(11) DEFAULT 0,
    `columnsize` int(11) DEFAULT 0,
    `pos_x` int(11) DEFAULT 0,
    `pos_y` int(11) DEFAULT 0,
    `width` int(11) DEFAULT 0,
    `height` int(11) DEFAULT 0,
    PRIMARY KEY (`screen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Subtitles` (
    `subtitle_id` int(11) NOT NULL AUTO_INCREMENT,
    `screen_id` int(11) NOT NULL,
    `preset_id` int(11) NOT NULL,
    `layer_id` int(11) NOT NULL,
    `object_id` int(11) NOT NULL,
    `subtitle_style` text NOT NULL,
    `update_date` datetime NOT NULL,
    PRIMARY KEY (`subtitle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Users` (
    `idx` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` varchar(255) NOT NULL,
    `user_name` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `auth_code` varchar(255) NOT NULL,
    `state` int(11) NOT NULL,
    `reg_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '등록일',
    `update_dt` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6) COMMENT '수정일',
    `invalid_cnt` int(11) NOT NULL,
    `led_key` varchar(255) NOT NULL,
    PRIMARY KEY (`idx`),
    UNIQUE KEY `IDX_82c76e6139f92f427f757fde78` (`auth_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `brightness_control_lists` (
    `list_id` int(11) NOT NULL AUTO_INCREMENT,
    `schedule_id` int(11) NOT NULL,
    `runtime` time NOT NULL COMMENT '실행시간',
    `Brightness_val` int(11) NOT NULL,
    PRIMARY KEY (`list_id`),
    KEY `schedule_id` (`schedule_id`),
    CONSTRAINT `brightness_control_lists_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `brightness_schedule` (`schedule_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `brightness_schedule` (
     `schedule_id` int(11) NOT NULL AUTO_INCREMENT,
     `sch_nm` varchar(255) NOT NULL,
     `sch_start_date` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '스케줄시작일',
     `sch_end_date` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '스케줄종료일',
     `run_day_week` varchar(13) DEFAULT NULL,
     PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `led_pwr_schedule` (
    `schedule_id` int(11) NOT NULL AUTO_INCREMENT,
    `sch_nm` varchar(255) NOT NULL,
    `sch_start_date` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '시작일',
    `sch_end_date` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '종료일',
    `time_pwr_on` time NOT NULL COMMENT '전원ON시간',
    `time_pwr_off` time NOT NULL COMMENT '전원OFF시간',
    `run_day_week` varchar(13) DEFAULT NULL,
    PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `playlist_contents` (
    `content_id` int(11) NOT NULL AUTO_INCREMENT,
    `type` varchar(100) NOT NULL,
    `cts_nm` varchar(255) NOT NULL,
    `cts_path` varchar(255) NOT NULL,
    `playtime` int(50) NOT NULL,
    `weather_fl` tinyint(4) NOT NULL,
    `air_info_fl` tinyint(4) NOT NULL,
    `stretch` tinyint(4) NOT NULL,
    `thumbnail_path` varchar(100) NOT NULL,
    PRIMARY KEY (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `playlist_schedule` (
    `schedule_id` int(11) NOT NULL AUTO_INCREMENT,
    `layer_id` int(11) NOT NULL,
    `sch_nm` varchar(255) NOT NULL,
    `sch_start_date` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '시작일',
    `sch_end_date` datetime(6) NOT NULL DEFAULT current_timestamp(6) COMMENT '종료일',
    `time_start` time NOT NULL,
    `time_end` time NOT NULL,
    `run_day_week` varchar(13) DEFAULT NULL,
    `playlist_id` int(11) NOT NULL,
    `preset_id` int(11) NOT NULL,
    PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Settings` (
    `program_id` int(11) NOT NULL AUTO_INCREMENT,
    `setting_key` varchar(100) NOT NULL,
    `setting_value` varchar(500) NOT NULL,
    `create_date` datetime NOT NULL DEFAULT current_timestamp(),
    `update_date` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('viewTopmostEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('viewTemphumiEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('ledCommType', 'TCP');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('ledPresetEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('ledInputSelectEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('ledBrightnessControlEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('ledPresetCount', '12');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('pwrControlEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('loginEn', '0');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('imgDefaultPlaytime', '10');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('externalinfoArea', '서울 서초구 방배로18길 5');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherSunny', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherManyCloudy', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherCloudy', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherRainSnow', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherSnow', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherRain', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('weatherShower', 'null');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('testPattern', '#FFFF8000,#FFFF0080,#FFC0C0C0,#FF808000,#FF800000,#FFFF00FF,#FF400080,#FF8080FF,#FFC08080,#FF008080,#FF0000FF,#FF808040');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('coords', '60,125');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('testPatternTime', '8');
INSERT INTO `Settings` (`setting_key`, `setting_value`) VALUES ('stationName', '동작대로 중앙차로');
