package com.sjxm.service;

import com.sjxm.vo.AccountVO;
import com.sjxm.vo.MessageVO;

import java.util.List;

public interface MessageService {
    List<AccountVO> userlist(Long uid);

    void setRead(List<Long> messageIds);

    MessageVO info(Long messageId);

    void update(MessageVO messageVO);

    List<MessageVO> getHistoryMessage(Long fromUid,Long toUid);

    List<MessageVO> getLikeMessage(Long uid);

    List<MessageVO> getCommentMessage(Long uid);

    void transMessageFromRedis2DB();
}
