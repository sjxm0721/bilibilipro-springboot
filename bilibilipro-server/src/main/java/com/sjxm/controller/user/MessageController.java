package com.sjxm.controller.user;


import com.sjxm.result.Result;
import com.sjxm.service.MessageService;
import com.sjxm.vo.AccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/message")
@Api(tags = "获取信息相关的接口")
public class MessageController implements Serializable {

    @Autowired
    private MessageService messageService;

    @GetMapping("/userlist")
    @ApiOperation("获取最近聊天的用户列表")
    public Result<List<AccountVO>> userlist(Long uid){

        List<AccountVO> list = messageService.userlist(uid);

        return Result.success(list);
    }

    @PostMapping("/isread")
    @ApiOperation("消息批量设置已读")
    public Result setRead(@RequestBody List<Long> messageIds){
        messageService.setRead(messageIds);
        return Result.success();
    }

}
