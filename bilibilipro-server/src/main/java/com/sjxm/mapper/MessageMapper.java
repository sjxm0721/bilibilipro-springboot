package com.sjxm.mapper;

import com.sjxm.entity.Message;
import com.sjxm.entity.MessageResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {

    List<Message> getHistoryMessage(Long uid1, Long uid2);


    void addMessage(Message messageTmp);


    List<MessageResult> getLatestCommuUser1(Long uid);


    List<MessageResult> getLatestCommuUser2(Long uid);

    @Select("select * from message where to_uid = #{fromUid} and type = '1' order by post_time")
    List<Message> getLikeMessage(Long fromUid);

    @Select("select * from message where message_id = #{messageId}")
    Message getByMessageId(Long messageId);

    void updateMessage(Message message);

    @Select("select * from message where to_uid = #{fromUid} and type = '3' order by post_time")
    List<Message> getCommentMessage(Long fromUid);
}
