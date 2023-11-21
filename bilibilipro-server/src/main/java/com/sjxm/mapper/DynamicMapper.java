package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.entity.Dynamic;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DynamicMapper {
    Page<Dynamic> getDynamicByUid(Long uid);

    @Select("select * from dynamic where dynamic_id = #{dynamicId}")
    Dynamic getDynamicById(Long dynamicId);

    void updateDynamic(Dynamic dynamic);

    @Insert("insert into dynamic(uid, post_time, text, video_id)" +
            " values(#{uid},#{postTime},#{text},#{videoId}) ")
    void addDynamic(Dynamic dynamic);

    @Delete("delete from dynamic where dynamic_id = #{dynamicId}")
    void deleteDynamic(Long dynamicId);

}
