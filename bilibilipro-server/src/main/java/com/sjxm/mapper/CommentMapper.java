package com.sjxm.mapper;

import com.github.pagehelper.Page;
import com.sjxm.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {


    Page<Comment> getCommentPageList(Integer videoId, Integer dynamicId);

    @Select("select * from comment where total_father_id = #{commentId}")
    List<Comment> getChildrenCommentList(Long commentId);

    @Select("select * from comment where father_id = #{commentId}")
    List<Comment> getLTChildrenCommentList(Long commentId);

    @Select("select * from comment where comment_id = #{commentId}")
    Comment getCommentById(Long commentId);

    void publishComment(Comment comment);

    void updateComment(Comment comment);

    @Delete("delete from comment where comment_id = #{commentId}")
    void deleteComment(Long commentId);

    @Select("select * from comment where dynamic_id = #{dynamicId}")
    List<Comment> getCommentListByDynamicId(Long dynamicId);
}
