package com.sjxm.service.impl;

import com.sjxm.constant.RedisExpireConstant;
import com.sjxm.dto.BarrageDTO;
import com.sjxm.entity.Barrage;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.BarrageMapper;
import com.sjxm.service.BarrageService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.BarrageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BarrageServiceImpl implements BarrageService {

    @Autowired
    private BarrageMapper barrageMapper;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 获取视频弹幕列表
     * @param videoId
     * @return
     */
    @Override
    public List<BarrageVO> list(Long videoId) {
            List<Barrage> list = barrageMapper.getBarrageList(videoId);
            List<BarrageVO> result = new ArrayList<>();
        for (Barrage barrage : list) {
            Long barrageId = barrage.getBarrageId();
            BarrageVO barrageVO = info(barrageId);
            result.add(barrageVO);
        }
            return result;
        }

    @Override
    public BarrageVO info(Long barrageId) {

        String redisKey = "barrage:barrageId="+barrageId;
        JacksonObjectMapper om = new JacksonObjectMapper();
        Object o = redisUtil.get(redisKey);
        if(o!=null){
            return om.convertValue(o, BarrageVO.class);
        }
        else{
            Barrage barrage = barrageMapper.getBarrageById(barrageId);
            BarrageVO barrageVO = new BarrageVO();
            BeanUtils.copyProperties(barrage,barrageVO);
            redisUtil.set(redisKey,barrageVO, RedisExpireConstant.BARRAGE_EXPIRE_TIME);
            return barrageVO;
        }
    }

    @Override
    public void update(BarrageVO barrageVO) {
        Barrage barrage = new Barrage();
        BeanUtils.copyProperties(barrageVO,barrage);
        barrageMapper.update(barrage);
        cleanCache(barrageVO.getBarrageId());
    }


    /**
     * 用户发送弹幕
     * @param barrageDTO
     */
    @Override
    public void send(BarrageDTO barrageDTO) {
        Barrage barrage = new Barrage();
        BeanUtils.copyProperties(barrageDTO,barrage);
        barrage.setPostTime(LocalDateTime.now());
        barrageMapper.addBarrage(barrage);
    }


    public void cleanCache(Long barrageId){
        String redisKey = "barrage:barrageId="+barrageId;
        redisUtil.del(redisKey);
    }

}
