package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.dto.VideoPageDTO;
import com.sjxm.entity.Video;
import com.sjxm.vo.VideoVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper {

    /**
     * 获取视频分页数据
     * @param videoPageDTO
     * @return
     */
    Page<Video> pageQuery(VideoPageDTO videoPageDTO);

    @Select("select  * from video where in_suggest = '1' limit 4")
    List<Video> getVideoInSuggest();

    @Select("select * from video where video_id = #{videoId}")
    Video getVideoInfoById(Long videoId);

    @Select("select * from video where uid = #{uid}")
    Page<Video> getVideoByUid(Long uid);

    void updateVideo(Video video);

    @Select("select * from video where title like concat('%',#{searchContent},'%') ")
    Page<Video> getVideoSearchPageList(String searchContent);

    @Insert("insert into video(title, src, post_time, barrage_num, last_time, click_num, uid, video_brief, like_num, coin_num, fav_num, tags, comment_num, poster) " +
            " values(#{title},#{src},#{postTime},#{barrageNum},#{lastTime},#{clickNum},#{uid},#{videoBrief},#{likeNum},#{coinNum},#{favNum},#{tags},#{commentNum},#{poster}) ")
    void addVideo(Video video);
}
