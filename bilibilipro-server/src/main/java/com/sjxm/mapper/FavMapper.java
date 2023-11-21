package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.entity.Fav;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavMapper {
    @Select("select * from fav where is_dic = '1' and uid = #{uid} ")
    List<Fav> getFatherFavListByUid(Long uid);

    @Select("select * from fav where is_dic = '0' and uid = #{uid} ")
    List<Fav> getFavVideoListByUid(Long uid);

    Page<Fav> pageQuery(Long uid,Long fatherDic);

    @Select("select * from fav where fav_id = #{favId}")
    Fav getFavInfoById(Long favId);

    @Insert("insert into fav( uid, is_dic, fav_title, fav_poster, is_public, create_time)" +
            "values(#{uid},#{isDic},#{favTitle},#{favPoster},#{isPublic},#{createTime})")
    void addFatherFav(Fav fav);

    @Insert("insert into fav(uid,video_id,father_dic,create_time) values(#{uid},#{videoId},#{fatherDic},#{createTime})")
    void addFavVideo(Fav fav);

    void updateFav(Fav fav);

    @Select("select * from fav where uid = #{uid} and video_id = #{videoId}")
    List<Fav> getFavByUidAndVideoId(Long uid, Long videoId);

    @Delete("delete from fav where uid = #{uid} and video_id = #{videoId}")
    void deleteFavByUidAndVideoId(Long uid, Long videoId);

    @Select("select * from fav where father_dic = #{favId}")
    List<Fav> getFavVideoListByFatherDic(Long favId);

    @Delete("delete from fav where fav_id=#{favId}")
    void deleteFav(Fav fav);
}
