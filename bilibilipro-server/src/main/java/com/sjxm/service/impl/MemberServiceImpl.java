package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.entity.Video;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.VideoMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.MemberService;
import com.sjxm.service.VideoService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private RedisUtil redisUtil;

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
        JacksonObjectMapper om = new JacksonObjectMapper();

        for (Video video : result) {
            Long videoId = video.getVideoId();
            String redisKey = "video:videoId="+videoId;
            Object o = redisUtil.get(redisKey);

            if(o!=null){
                VideoVO videoVO = om.convertValue(o, VideoVO.class);
                list.add(videoVO);
            }
            else{
                VideoVO videoVO = new VideoVO();
                BeanUtils.copyProperties(video,videoVO);
                redisUtil.set(redisKey,videoVO);
                list.add(videoVO);
            }
        }

        return new PageResult(total,list);
    }
}
