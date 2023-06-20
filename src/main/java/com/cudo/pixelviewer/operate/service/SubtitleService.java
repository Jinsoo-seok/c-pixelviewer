package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface SubtitleService {

    Map<String, Object> postSubtitle(Map<String, Object> param);

    Map<String, Object> patchSubtitleText(Map<String, Object> param);

    Map<String, Object> patchSubtitleLocation(Map<String, Object> param);

    Map<String, Object> patchSubtitleSize(Map<String, Object> param);

    Map<String, Object> patchSubtitleStyle(Map<String, Object> param);

    Map<String, Object> patchSubtitleScroll(Map<String, Object> param);

}