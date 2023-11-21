package com.sjxm.service;

import com.sjxm.dto.VideoDTO;
import com.sjxm.dto.VideoPageDTO;
import com.sjxm.result.PageResult;
import com.sjxm.vo.VideoInfoVO;
import com.sjxm.vo.VideoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface VideoService {

    PageResult page(VideoPageDTO videoPageDTO);

    List<VideoVO> homesuggest();


    void update(VideoVO videoVO);

    VideoVO info(Long videoId);


    void click(Long videoId);

    void transVideoFromRedis2DB();

    PageResult search(String searchContent,Integer page,Integer pageSize,Integer order);

    VideoInfoVO getVideoInfo(File file);

    void add(VideoDTO videoDTO);
}
