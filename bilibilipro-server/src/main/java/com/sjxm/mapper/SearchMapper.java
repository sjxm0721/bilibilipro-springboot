package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.entity.Search;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SearchMapper {
    @Select("select * from search where search_id = #{searchId}")
    Search getSearchById(Long searchId);

    @Select("select * from search")
    Page<Search> getSearchPageList();

    @Select("select * from search where search_content = #{searchContent}")
    Search getSearchBySearchContent(String searchContent);

    void addSearch(Search search);

    void updateSearch(Search search);
}
