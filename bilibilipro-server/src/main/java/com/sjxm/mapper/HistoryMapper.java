package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.entity.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HistoryMapper {

    @Select("select * from history where uid = #{uid} ")
    Page<History> page(Long uid);

    @Select("select * from history where uid=#{uid} and video_id=#{videoId}")
    History getHistoryByUidAndVideoId(History history);

    @Insert("insert into history(uid, video_id, create_time) values(#{uid},#{videoId},#{createTime}) ")
    void addHistory(History history);


    void updateHistory(History history);
}
