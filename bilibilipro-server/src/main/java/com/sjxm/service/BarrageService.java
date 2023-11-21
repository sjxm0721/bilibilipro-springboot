package com.sjxm.service;

import com.sjxm.dto.BarrageDTO;
import com.sjxm.entity.Barrage;
import com.sjxm.vo.BarrageVO;

import java.util.List;

public interface BarrageService {
    List<BarrageVO> list(Long videoId);

    BarrageVO info(Long barrageId);

    void update(BarrageVO barrageVO);

    void send(BarrageDTO barrageDTO);
}
