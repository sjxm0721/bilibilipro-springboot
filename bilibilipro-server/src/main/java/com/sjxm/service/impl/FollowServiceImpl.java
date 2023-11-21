package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.dto.FollowDTO;
import com.sjxm.entity.Follow;
import com.sjxm.mapper.FollowMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.AccountService;
import com.sjxm.service.FollowService;
import com.sjxm.vo.AccountVO;
import com.sjxm.vo.FollowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private AccountService accountService;

    /**
     * 获取关注列表分页数据
     * @param page
     * @param pageSize
     * @param uid
     * @return
     */
    @Override
    public PageResult follow(Integer page, Integer pageSize, Long uid) {

            PageHelper.startPage(page,pageSize,"create_time desc");

            Page<Follow> pageResult = followMapper.followPageQuery(uid);

            long total = pageResult.getTotal();
            List<Follow> list = pageResult.getResult();

            List<FollowVO> result = new ArrayList<>();

            for (Follow follow : list) {

                FollowVO followVO = new FollowVO();
                BeanUtils.copyProperties(follow,followVO);
                Long followedUid = followVO.getFollowedUid();
                AccountVO accountVO = accountService.info(followedUid);
                followVO.setFollowedName(accountVO.getAccountName());
                followVO.setFollowedAvatar(accountVO.getAvatar());
                followVO.setFollowedBrief(accountVO.getAccountBrief());

                result.add(followVO);
            }

            return new PageResult(total, result);
    }

    /**
     * 获取粉丝列表分页数据
     * @param page
     * @param pageSize
     * @param uid
     * @return
     */
    @Override
    public PageResult fans(Integer page, Integer pageSize, Long uid) {

            PageHelper.startPage(page,pageSize,"create_time desc");

            Page<Follow> pageResult = followMapper.fansPageQuery(uid);

            long total = pageResult.getTotal();
            List<Follow> list = pageResult.getResult();

            List<FollowVO> result = new ArrayList<>();

            for (Follow follow : list) {

                FollowVO followVO = new FollowVO();
                BeanUtils.copyProperties(follow,followVO);
                Long followerUid = followVO.getFollowerUid();
                AccountVO accountVO = accountService.info(followerUid);
                followVO.setFollowerName(accountVO.getAccountName());
                followVO.setFollowerAvatar(accountVO.getAvatar());
                followVO.setFollowerBrief(accountVO.getAccountBrief());

                result.add(followVO);
            }
            return new PageResult(total, result);
    }

    /**
     * 点击关注
     * @param followDTO
     */
    @Override
    @Transactional
    public void add(FollowDTO followDTO) {
        Long followedUid = followDTO.getFollowedUid();
        Long followerUid = followDTO.getFollowerUid();
        Follow follow = new Follow();
        BeanUtils.copyProperties(followDTO,follow);
        follow.setCreateTime(LocalDateTime.now());
        followMapper.addFollow(follow);
        //更新关注者被关注者数量相应关注与粉丝数量
        AccountVO followedAccountVO = accountService.info(followedUid);
        followedAccountVO.setFansNum(followedAccountVO.getFansNum()+1);
       accountService.update(followedAccountVO);

        AccountVO followerAccountVO = accountService.info(followerUid);
        followerAccountVO.setFollowNum(followerAccountVO.getFollowNum()+1);
        accountService.update(followerAccountVO);

    }

    /**
     * 查询是否关注
     * @param followerUid
     * @param followedUid
     * @return
     */
    @Override
    public Boolean isfollow(Long followerUid, Long followedUid) {

        List<Follow> list = followMapper.isFollow(followerUid,followedUid);

        if(list.size() == 0){
            //未关注
            return false;
        }
        //已关注
        return true;
    }

    /**
     * 点击取消关注按钮的回调
     * @param followDTO
     */
    @Transactional
    @Override
    public void cancel(FollowDTO followDTO) {
        Long followedUid = followDTO.getFollowedUid();
        Long followerUid = followDTO.getFollowerUid();

        followMapper.deleteFollow(followerUid,followedUid);

        //更新关注者被关注者数量相应关注与粉丝数量
        AccountVO followedAccountVO = accountService.info(followedUid);
        followedAccountVO.setFansNum(followedAccountVO.getFansNum()-1);
        accountService.update(followedAccountVO);

        AccountVO followerAccountVO = accountService.info(followerUid);
        followerAccountVO.setFollowNum(followerAccountVO.getFollowNum()-1);
        accountService.update(followerAccountVO);


    }
}
