package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.entity.Follow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Select("select * from follow where follower_uid = #{uid}")
    Page<Follow> followPageQuery(Long uid);

    @Select("select * from follow where followed_uid = #{uid}")
    Page<Follow> fansPageQuery(Long uid);

    @Insert("insert into follow(follower_uid, followed_uid, create_time) values(#{followerUid},#{followedUid},#{createTime})")
    void addFollow(Follow follow);

    @Select("select * from follow where follower_uid = #{followerUid} and followed_uid = #{followedUid}")
    List<Follow> isFollow(Long followerUid, Long followedUid);

    @Delete("delete from follow where follower_uid=#{followerUid} and followed_uid=#{followedUid}")
    void deleteFollow(Long followerUid, Long followedUid);
}
