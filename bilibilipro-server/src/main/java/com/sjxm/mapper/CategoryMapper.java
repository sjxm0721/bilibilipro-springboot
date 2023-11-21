package com.sjxm.mapper;

import com.sjxm.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("select * from category where home_show = '1'")
    List<Category> getHomeCategory();

    @Select("select * from category where category_id = #{categoryId}")
    Category getCategoryById(Long categoryId);

    void updateCategory(Category category);
}
