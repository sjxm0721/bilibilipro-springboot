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


    Long addVideo(Video video);
}
