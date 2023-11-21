package com.sjxm.service;

import com.sjxm.dto.FavDTO;
import com.sjxm.dto.FavVideoDTO;
import com.sjxm.entity.Fav;
import com.sjxm.result.PageResult;
import com.sjxm.vo.FavVo;

import java.util.List;

public interface FavService {
    List<FavVo> fatherlist(Long uid);

    PageResult page(Long uid,Integer page, Integer pageSize, Long fatherDic);

    void addlist(FavDTO favDTO);

    void addVideo2Fav(FavVideoDTO favVideoDTO);

    FavVo info(Long favId);
    
    void update(FavVo favVo);

    List<FavVo> getTotalFavVideo(Long uid);

    void deleteVideoFromAllFav(Long uid, Long videoId);

    void deleteVideoFromFav(Fav fav);

    void deleteFatherFav(Long favId);
}
