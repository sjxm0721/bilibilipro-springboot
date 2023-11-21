package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.constant.MessageConstant;
import com.sjxm.dto.VideoDTO;
import com.sjxm.dto.VideoPageDTO;
import com.sjxm.entity.Video;
import com.sjxm.exception.VideoNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.VideoMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.AccountService;
import com.sjxm.service.CategoryService;
import com.sjxm.service.VideoService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.AccountVO;
import com.sjxm.vo.CategoryVO;
import com.sjxm.vo.VideoInfoVO;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AccountService accountService;

    /**
     * 获取视频分页数据
     * @param videoPageDTO
     * @return
     */
    @Override
    public PageResult page(VideoPageDTO videoPageDTO) {

        PageHelper.startPage(videoPageDTO.getPage(),videoPageDTO.getPageSize());

        Page<Video> page = videoMapper.pageQuery(videoPageDTO);

        long total = page.getTotal();

        List<Video> result = page.getResult();

        List<VideoVO> list = new ArrayList<>();

        for (Video video : result) {
            Long videoId = video.getVideoId();
            VideoVO videoVO = info(videoId);
            list.add(videoVO);
            }
        return new PageResult(total,list);
    }

    @Override
    public List<VideoVO> homesuggest() {

        List<Video> videoInSuggest = videoMapper.getVideoInSuggest();

        List<VideoVO> list = new ArrayList<>();

        for (Video video : videoInSuggest) {
            Long videoId = video.getVideoId();
            VideoVO videoVO = info(videoId);
            list.add(videoVO);
            }
        return list;
    }

    @Override
    public void update(VideoVO videoVO) {
        Long videoId = videoVO.getVideoId();
        String redisKey = "video:videoId="+videoId;
        redisUtil.set(redisKey,videoVO);
    }

    @Override
    public VideoVO info(Long videoId) {

        String redisKey = "video:videoId="+videoId;
        Object o = redisUtil.get(redisKey);
        JacksonObjectMapper om = new JacksonObjectMapper();
        if(o!=null){
            return om.convertValue(o, VideoVO.class);
        }
        else{
            Video video = videoMapper.getVideoInfoById(videoId);
            if(video == null){
                throw new VideoNotFoundException(MessageConstant.VIDEO_NOT_FOUND);
            }
            VideoVO videoVO = new VideoVO();
            BeanUtils.copyProperties(video,videoVO);
            Long uid = videoVO.getUid();
            String tags = video.getTags();
            List<CategoryVO> categoryTags = videoVO.getCategoryTags();
            if(categoryTags == null){
                categoryTags = new ArrayList<>();
            }
            if(tags!=null){
                String[] tagArray = tags.split(",");
                for (String str : tagArray) {
                    categoryTags.add(categoryService.info(Long.valueOf(str)));
                }
            }
            AccountVO accountVO = accountService.info(uid);
            videoVO.setAccountName(accountVO.getAccountName());
            videoVO.setCategoryTags(categoryTags);
            redisUtil.set(redisKey,videoVO);
            return videoVO;
        }
    }

    @Override
    @Transactional
    public void click(Long videoId) {

        VideoVO videoVO = info(videoId);
        videoVO.setClickNum(videoVO.getClickNum()+1);
        update(videoVO);
        accountService.click(videoVO.getUid());
    }


    /**
     * 将redis数据存入数据库
     */
    @Override
    @Transactional
    public void transVideoFromRedis2DB(){
        Set<String> keys = redisUtil.keys("video:videoId=" + "*");
        JacksonObjectMapper om = new JacksonObjectMapper();
        for (String key : keys) {
            Object o = redisUtil.get(key);
            VideoVO videoVO = om.convertValue(o, VideoVO.class);
            Video video = new Video();
            BeanUtils.copyProperties(videoVO,video);
            videoMapper.updateVideo(video);
            redisUtil.del(key);
        }
    }

    /**
     * 用户搜索视频
     * @param searchContent
     * @param page
     * @param pageSize
     * @param order
     * @return
     */
    @Override
    public PageResult search(String searchContent, Integer page, Integer pageSize, Integer order) {
        List<String> orderChosed = new ArrayList<>();
        orderChosed.add("video_id asc");
        orderChosed.add("click_num desc");
        orderChosed.add("post_time desc");
        orderChosed.add("barrage_num desc");
        orderChosed.add("fav_num desc");

        PageHelper.startPage(page,pageSize,orderChosed.get(order));
        Page<Video> pageResult = videoMapper.getVideoSearchPageList(searchContent);

        List<VideoVO> list = new ArrayList<>();

        List<Video> result = pageResult.getResult();
        long total = pageResult.getTotal();

        for (Video video : result) {
            Long videoId = video.getVideoId();
            VideoVO videoVO = info(videoId);
            list.add(videoVO);
        }

        return new PageResult(total,list);
    }

    /**
     * 获取视频元数据信息
     * @param file
     * @return
     */
    @Override
    public VideoInfoVO getVideoInfo(File file) {
        VideoInfoVO videoInfoVO = new VideoInfoVO();
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = FFmpegFrameGrabber.createDefault(file);
            // 启动 FFmpeg
            grabber.start();

            // 读取视频帧数
            videoInfoVO.setLengthInFrames(grabber.getLengthInVideoFrames());

            // 读取视频帧率
            videoInfoVO.setFrameRate(grabber.getVideoFrameRate());

            // 读取视频秒数
            videoInfoVO.setDuration(grabber.getLengthInTime() / 1000000.00);

            // 读取视频宽度
            videoInfoVO.setWidth(grabber.getImageWidth());

            // 读取视频高度
            videoInfoVO.setHeight(grabber.getImageHeight());


            videoInfoVO.setAudioChannel(grabber.getAudioChannels());

            videoInfoVO.setVideoCode(grabber.getVideoCodecName());

            videoInfoVO.setAudioCode(grabber.getAudioCodecName());

            videoInfoVO.setSampleRate(grabber.getSampleRate());
            return videoInfoVO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FFmpegFrameGrabber.Exception e) {
                log.error("getVideoInfo grabber.release failed 获取文件信息失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 用户上传视频
     * @param videoDTO
     */
    @Override
    public void add(VideoDTO videoDTO) {

        Video video = new Video();
        BeanUtils.copyProperties(videoDTO,video);
        Long[] tags = videoDTO.getTags();

        StringJoiner joiner = new StringJoiner(",");
        for (long value : tags) {
            joiner.add(String.valueOf(value));
        }

        String newTags = joiner.toString();

        video.setPostTime(LocalDateTime.now());
        video.setBarrageNum(0);
        video.setClickNum(0);
        video.setLikeNum(0);
        video.setCoinNum(0);
        video.setFavNum(0);
        video.setCommentNum(0);
        video.setTags(newTags);
        videoMapper.addVideo(video);

    }
}
