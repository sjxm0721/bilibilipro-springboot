package com.sjxm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.dto.HistoryDTO;
import com.sjxm.entity.Account;
import com.sjxm.entity.History;
import com.sjxm.entity.Video;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.AccountMapper;
import com.sjxm.mapper.HistoryMapper;
import com.sjxm.mapper.VideoMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.AccountService;
import com.sjxm.service.HistoryService;
import com.sjxm.service.VideoService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.AccountVO;
import com.sjxm.vo.HistoryRedisVO;
import com.sjxm.vo.HistoryVO;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoService videoService;


    /**
     * 获取用户浏览记录分页数据
     * @param uid
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageResult page(Long uid, Integer page, Integer pageSize) throws JsonProcessingException {

        String redisKey = "history:uid="+uid;
        long start = (long) (page - 1) *pageSize;
        long end = (long) page *pageSize-1;
        JacksonObjectMapper om = new JacksonObjectMapper();

        List<HistoryVO> list = new ArrayList<>();

        List<History> historyList = new ArrayList<>();

        long total = 0;

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisUtil.zReverseRangeWithScores(redisKey, start, end);
        if(!typedTuples.isEmpty()){
            for (ZSetOperations.TypedTuple<Object> typedTuple : typedTuples) {
                Object value = typedTuple.getValue();
                Double score = typedTuple.getScore();
                HistoryRedisVO historyRedisVO = om.convertValue(value, HistoryRedisVO.class);
                History history = new History();
                BeanUtils.copyProperties(historyRedisVO,history);
                Instant instant = null;
                if (score != null) {
                    instant = Instant.ofEpochSecond(score.longValue());
                }
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                history.setCreateTime(localDateTime);
                historyList.add(history);
            }
        }
        else {
            PageHelper.startPage(page, pageSize, "create_time desc");
            Page<History> pageResult = historyMapper.page(uid);
            historyList = pageResult.getResult();
            total = pageResult.getTotal();
        }

            for (History history : historyList) {
                HistoryVO historyVO = new HistoryVO();
                BeanUtils.copyProperties(history, historyVO);
                Long videoId = historyVO.getVideoId();
                VideoVO videoVO = videoService.info(videoId);
                historyVO.setVideoPoster(videoVO.getPoster());
                historyVO.setVideoTitle(videoVO.getTitle());
                historyVO.setAccountName(videoVO.getAccountName());
                historyVO.setLastTime(videoVO.getLastTime());
                list.add(historyVO);
                if(typedTuples.isEmpty())
                {
                    HistoryRedisVO historyRedisVO = new HistoryRedisVO();
                    BeanUtils.copyProperties(historyVO, historyRedisVO);
                    LocalDateTime createTime = historyVO.getCreateTime();
                    long timestamp = createTime.atZone(ZoneId.systemDefault()).toEpochSecond();
                    redisUtil.zAdd(redisKey, historyRedisVO, timestamp);
                }
            }
        return new PageResult(total,list);
    }

    /**
     * 用户增加浏览记录
     * @param historyDTO
     */
    @Override
    public void add(HistoryDTO historyDTO) throws JsonProcessingException {
        Long uid = historyDTO.getUid();
        String redisKey = "history:uid="+uid;
        HistoryRedisVO historyRedisVO = new HistoryRedisVO();
        BeanUtils.copyProperties(historyDTO,historyRedisVO);
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        page(uid,1,10);
        redisUtil.zAdd(redisKey,historyRedisVO,timestamp);
    }

    /**
     * 将redis中的用户浏览数据添加入数据库
     */
    @Override
    @Transactional
    public void transHistoryFromRedis2DB(){
        Set<String> keys = redisUtil.keys("history:uid=" + "*");
        JacksonObjectMapper om = new JacksonObjectMapper();
        if (keys != null) {
            for (String key : keys) {
                Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisUtil.zRangeWithScores(key, 0, -1);
                if(!typedTuples.isEmpty()){
                    for (ZSetOperations.TypedTuple<Object> typedTuple : typedTuples) {
                        Double score = typedTuple.getScore();
                        Object value = typedTuple.getValue();
                        Instant instant = null;
                        if (score != null) {
                            instant = Instant.ofEpochSecond(score.longValue());
                        }
                        ZoneId zoneId = ZoneId.systemDefault();
                        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                        HistoryRedisVO historyRedisVO = om.convertValue(value, HistoryRedisVO.class);
                        History history = new History();
                        BeanUtils.copyProperties(historyRedisVO,history);
                        history.setCreateTime(localDateTime);
                        History historyByUidAndVideoId = historyMapper.getHistoryByUidAndVideoId(history);
                        if(historyByUidAndVideoId == null){
                            //添加操作
                            historyMapper.addHistory(history);
                        }
                        else{
                            //更新操作
                            historyMapper.updateHistory(history);
                        }
                    }
                }
                redisUtil.del(key);
            }
        }
    }
}
