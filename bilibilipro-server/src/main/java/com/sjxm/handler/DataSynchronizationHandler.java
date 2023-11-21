package com.sjxm.handler;

import com.sjxm.service.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSynchronizationHandler implements DisposableBean {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private VideoService videoService;

    // 添加你的数据同步逻辑 spring注销时调用
    @Override
    public void destroy() throws Exception {
        accountService.transAccountFromRedis2DB();
        commentService.transCommentFromRedis2DB();
        historyService.transHistoryFromRedis2DB();
        likeService.transLikeFromRedis2DB();
        videoService.transVideoFromRedis2DB();
    }
}
