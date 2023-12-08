package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.entity.Video;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.VideoMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.MemberService;
import com.sjxm.service.VideoService;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoService videoService;

    /**
     * 根据uid获取用户投稿信息
     * @param uid
     * @param order
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageResult videopage(Long uid, Integer order, Integer page, Integer pageSize) {

        videoService.transVideoFromRedis2DB();
        List<String> orderBy = new ArrayList<>();
        orderBy.add("post_time desc");
        orderBy.add("click_num desc");
        orderBy.add("fav_num desc");
        PageHelper.startPage(page,pageSize, orderBy.get(order));
        Page<Video> pageResult = videoMapper.getVideoByUid(uid);

        List<Video> result = pageResult.getResult();
        long total = pageResult.getTotal();

        List<VideoVO> list = new ArrayList<>();

        for (Video video : result) {
            VideoVO videoVO = videoService.info(video.getVideoId());
            list.add(videoVO);
        }

        return new PageResult(total,list);
    }
}
