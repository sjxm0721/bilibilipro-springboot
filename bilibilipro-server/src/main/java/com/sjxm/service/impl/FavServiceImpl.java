package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.constant.MessageConstant;
import com.sjxm.constant.RedisExpireConstant;
import com.sjxm.dto.FavDTO;
import com.sjxm.dto.FavVideoDTO;
import com.sjxm.entity.Fav;
import com.sjxm.exception.FavNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.FavMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.AccountService;
import com.sjxm.service.FavService;
import com.sjxm.service.VideoService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.AccountVO;
import com.sjxm.vo.FavVo;
import com.sjxm.vo.LikeVO;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FavServiceImpl implements FavService {

    @Autowired
    private FavMapper favMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取收藏夹列表数据
     * @param uid
     * @return
     */
    @Override
    public List<FavVo> fatherlist(Long uid) {
        AccountVO accountVO = accountService.info(uid);
        String name = accountVO.getAccountName();
        List<Fav> list = favMapper.getFatherFavListByUid(uid);

        List<FavVo> result = new ArrayList<>();

        for (Fav fav : list) {
            FavVo favVo = new FavVo();
            BeanUtils.copyProperties(fav,favVo);
            favVo.setName(name);
            result.add(favVo);
        }
        return result;
    }

    /**
     * 获取分页收藏列表数据
     * @param page
     * @param pageSize
     * @param fatherDic
     * @return
     */
    @Override
    public PageResult page(Long uid,Integer page, Integer pageSize, Long fatherDic) {
        PageHelper.startPage(page,pageSize,"create_time desc");

        Page<Fav> pageResult = favMapper.pageQuery(uid,fatherDic);

        long total = pageResult.getTotal();

        List<Fav> result = pageResult.getResult();

        List<FavVo> list = new ArrayList<>();


        for (Fav fav : result) {
            Long favId = fav.getFavId();
            FavVo favVo = info(favId);
            list.add(favVo);
        }

        return new PageResult(total,list);
    }

    /**
     * 新建收藏夹
     * @param favDTO
     */
    @Override
    public void addlist(FavDTO favDTO) {
        Fav fav = new Fav();
        BeanUtils.copyProperties(favDTO,fav);
        fav.setCreateTime(LocalDateTime.now());
        favMapper.addFatherFav(fav);
    }

    /**
     * 视频数据添加入收藏夹
     * @param favVideoDTO
     */
    @Override
    @Transactional
    public void addVideo2Fav(FavVideoDTO favVideoDTO) {
        Fav fav = new Fav();
        BeanUtils.copyProperties(favVideoDTO,fav);
        fav.setCreateTime(LocalDateTime.now());
        Long fatherDic = favVideoDTO.getFatherDic();
        FavVo fatherFavVo = info(fatherDic);
        fatherFavVo.setFavNum(fatherFavVo.getFavNum()+1);
        update(fatherFavVo);
        Long videoId = fav.getVideoId();
        VideoVO videoVO = videoService.info(videoId);
        videoVO.setFavNum(videoVO.getFavNum()+1);
        videoService.update(videoVO);
        favMapper.addFavVideo(fav);
        cleanCacheByUid(fav.getUid());
    }

    /**
     * 获取收藏相关信息
     * @param favId
     * @return
     */
    @Override
    public FavVo info(Long favId) {
        String redisKey = "fav:favId="+favId;
        Object o = redisUtil.get(redisKey);
        JacksonObjectMapper om = new JacksonObjectMapper();
        if(o!=null){
            return om.convertValue(o, FavVo.class);
        }
        else{
            Fav fav = favMapper.getFavInfoById(favId);
            if(fav == null){
                throw new FavNotFoundException(MessageConstant.FAV_NOT_FOUND);
            }
            FavVo favVo = new FavVo();
            BeanUtils.copyProperties(fav,favVo);
            Long fatherDic = favVo.getFatherDic();
            if(fatherDic == null){
                //收藏夹
                Long uid = favVo.getUid();
                AccountVO accountVO = accountService.info(uid);
                favVo.setName(accountVO.getAccountName());
            }
            else{
                //收藏视频信息
                FavVo fatherFav = info(fatherDic);
                favVo.setFatherDicTitle(fatherFav.getFavTitle());
                Long videoId = favVo.getVideoId();
                VideoVO videoVO = videoService.info(videoId);
                favVo.setVideoPoster(videoVO.getPoster());
                favVo.setVideoTitle(videoVO.getTitle());
                favVo.setLastTime(videoVO.getLastTime());
                Long uid = videoVO.getUid();
                AccountVO accountVO = accountService.info(uid);
                favVo.setVideoUName(accountVO.getAccountName());
            }
            redisUtil.set(redisKey,favVo, RedisExpireConstant.FAV_EXPIRE_TIME);
            return favVo;
        }
    }

    /**
     * 更新收藏
     * @param favVo
     */
    @Override
    @Transactional
    public void update(FavVo favVo) {
        Fav fav = new Fav();
        BeanUtils.copyProperties(favVo,fav);
        favMapper.updateFav(fav);
        cleanCacheByFavId(favVo.getFavId());
    }

    /**
     * 获得用户所有收藏视频的列表
     * @param uid
     * @return
     */
    @Override
    public List<FavVo> getTotalFavVideo(Long uid) {
        String redisKey = "fav:uid="+uid;
        Set<Object> objects = redisUtil.sGet(redisKey);
        JacksonObjectMapper om = new JacksonObjectMapper();
        List<FavVo> list = new ArrayList<>();
        if (objects != null && !objects.isEmpty()) {
            for (Object object : objects) {
                Long favId = om.convertValue(object, Long.class);
                FavVo favVo = info(favId);
                list.add(favVo);
            }
        }
        else{
            List<Fav> listTmp = favMapper.getFavVideoListByUid(uid);
            for (Fav fav : listTmp) {
                redisUtil.sSetAndTime(redisKey,RedisExpireConstant.FAV_EXPIRE_TIME,fav.getFavId());
                list.add(info(fav.getFavId()));
            }
        }
        return list;
    }


    /**
     * 删除收藏的所有某视频
     * @param uid
     * @param videoId
     */
    @Override
    @Transactional
    public void deleteVideoFromAllFav(Long uid, Long videoId) {
        List<Fav> list = favMapper.getFavByUidAndVideoId(uid,videoId);
        if(list!=null && list.size()>0){
            for (Fav fav : list) {
                deleteVideoFromFav(fav);
            }
        }
        cleanCacheByUid(uid);
    }

    /**
     * 从收藏夹中删除某一个视频
     * @param fav
     */
    @Override
    public void deleteVideoFromFav(Fav fav) {
        FavVo favVo = info(fav.getFatherDic());
        favVo.setFavNum(favVo.getFavNum()-1);
        update(favVo);
        VideoVO videoVO = videoService.info(fav.getVideoId());
        videoVO.setFavNum(videoVO.getFavNum()-1);
        videoService.update(videoVO);
        favMapper.deleteFav(fav);
        cleanCacheByFavId(fav.getFavId());
    }

    /**
     * 删除收藏夹
     * @param favId
     */
    @Override
    public void deleteFatherFav(Long favId) {
        Fav fav = favMapper.getFavInfoById(favId);
        Long uid = fav.getUid();
        List<Fav> list = favMapper.getFavVideoListByFatherDic(favId);
        for (Fav favTmp : list) {
            deleteVideoFromFav(favTmp);
        }
        favMapper.deleteFav(fav);
        cleanCacheByFavId(fav.getFavId());
        cleanCacheByUid(uid);
    }

    public void cleanCacheByFavId(Long favId){
        String redisKey = "fav:favId="+favId;
        redisUtil.del(redisKey);
    }
    public void cleanCacheByUid(Long uid){
        String redisKey = "fav:uid="+uid;
        redisUtil.del(redisKey);
    }
}
