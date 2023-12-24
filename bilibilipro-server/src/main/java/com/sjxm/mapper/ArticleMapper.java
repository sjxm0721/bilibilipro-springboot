package com.sjxm.mapper;

import com.sjxm.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {
    void add(Article article);

    List<Article> list(Integer category, Long uid,Integer chosedTime,Integer chosedOrder, String title);

    @Select("select * from article where article_id = #{articleId}")
    Article getArticleByArticleId(Long articleId);
}
