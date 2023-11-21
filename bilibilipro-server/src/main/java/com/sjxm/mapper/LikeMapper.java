package com.sjxm.mapper;

import com.sjxm.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LikeMapper {

    @Select("select * from `like` where from_uid = #{uid} and type = #{type}")
    List<Like> getLikeList(Long uid, char type);

    void updateLike(Like like);

    @Insert("insert into `like`(from_uid, to_uid, type, video_id, dynamic_id, comment_id, barrage_id, status, create_time, update_time) " +
            "values(#{fromUid},#{toUid},#{type},#{videoId},#{dynamicId},#{commentId},#{barrageId},#{status},#{createTime},#{updateTime})")
    void addLike(Like like);

    @Delete("delete from `like` where status = '1'")
    void deleteLikeCanceled();
}
