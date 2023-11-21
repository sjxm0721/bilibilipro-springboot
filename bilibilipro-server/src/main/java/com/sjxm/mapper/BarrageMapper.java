package com.sjxm.mapper;

import com.sjxm.entity.Barrage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BarrageMapper {
    @Select("select * from barrage where video_id = #{videoId} order by time")
    List<Barrage> getBarrageList(Long videoId);

    @Insert("insert into barrage(time, uid, text, post_time, video_id, type, color) values(#{time},#{uid},#{text},#{postTime},#{videoId},#{type},#{color})")
    void addBarrage(Barrage barrage);

    @Select("select * from barrage where barrage_id = #{barrageId}")
    Barrage getBarrageById(Long barrageId);

    void update(Barrage barrage);
}
