package com.cudo.pixelviewer.user.mapper;

import com.cudo.pixelviewer.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {

    Map<String, Object> postLogin2 (Map<String, Object> param);
    UserVo postLogin (Map<String, Object> param);

}
