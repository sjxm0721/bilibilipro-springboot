package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.constant.MessageConstant;
import com.sjxm.entity.Search;
import com.sjxm.exception.SearchNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.SearchMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.SearchService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SearchMapper searchMapper;

    private static final Map<String,Long> searchMap = new ConcurrentHashMap<>();

    /**
     * 添加搜索数据
     * @param searchContent
     */
    @Override
    public void add(String searchContent) {
        searchContent = searchContent.trim();
        Long searchId = searchMap.get(searchContent);
        if(searchId == null){
            //redis中不存在
            Search search = searchMapper.getSearchBySearchContent(searchContent);
            if(search == null){
                //DB中不存在
                Search searchTmp = new Search();
                searchTmp.setSearchContent(searchContent);
                searchTmp.setCreateTime(LocalDateTime.now());
                searchTmp.setUpdateTime(LocalDateTime.now());
                searchTmp.setSearchNum(1);
                searchMapper.addSearch(searchTmp);
            }
            else{
                search.setSearchNum(search.getSearchNum()+1);
                search.setUpdateTime(LocalDateTime.now());
                searchMapper.updateSearch(search);
            }
        }
        else{
            //存在redis中
            SearchVO searchVO = info(searchId);
            searchVO.setSearchNum(searchVO.getSearchNum()+1);
            update(searchVO);
        }
    }

    /**
     * 获取热搜数据
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageResult list(Integer page,Integer pageSize) {
        PageHelper.startPage(page,pageSize,"search_num desc");
        Page<Search> pageResult = searchMapper.getSearchPageList();

        List<Search> list = pageResult.getResult();
        long total = pageResult.getTotal();
        List<SearchVO> result = new ArrayList<>();

        for (Search search : list) {
            Long searchId = search.getSearchId();
            SearchVO searchVO = info(searchId);
            result.add(searchVO);
        }

        Collections.sort(result, new Comparator<SearchVO>() {
            @Override
            public int compare(SearchVO o1, SearchVO o2) {
                return Integer.compare(o2.getSearchNum(),o1.getSearchNum());
            }
        });

        return new PageResult(total,result);
    }

    /**
     * 获取搜索信息
     * @param searchId
     * @return
     */
    @Override
    public SearchVO info(Long searchId) {
        String redisKey = "search:searchId="+searchId;
        Object o = redisUtil.get(redisKey);
        JacksonObjectMapper om = new JacksonObjectMapper();
        if(o!=null){
            return om.convertValue(o, SearchVO.class);
        }
        else{
            Search search = searchMapper.getSearchById(searchId);
            if(search == null){
                throw new SearchNotFoundException(MessageConstant.SEARCH_NOT_FOUND);
            }
            SearchVO searchVO = new SearchVO();
            BeanUtils.copyProperties(search,searchVO);
            searchMap.put(searchVO.getSearchContent(),searchVO.getSearchId());
            redisUtil.set(redisKey,searchVO);
            return searchVO;
        }
    }

    /**
     * 更新搜索信息
     * @param searchVO
     */
    @Override
    public void update(SearchVO searchVO) {
        Long searchId = searchVO.getSearchId();
        String redisKey = "search:searchId="+searchId;
        searchMap.computeIfAbsent(searchVO.getSearchContent(), k -> searchVO.getSearchId());
        redisUtil.set(redisKey,searchVO);
    }

    /**
     * 将redis中的search数据存储入DB
     */
    @Override
    public void transSearchFromRedis2DB() {
        Set<String> keys = redisUtil.keys("search:searchId=" + "*");
        JacksonObjectMapper om = new JacksonObjectMapper();
        if (keys != null) {
            for (String key : keys) {
                Object o = redisUtil.get(key);
                SearchVO searchVO = om.convertValue(o, SearchVO.class);
                Search search = new Search();
                BeanUtils.copyProperties(searchVO,search);
                searchMapper.updateSearch(search);
                redisUtil.del(key);
                searchMap.remove(search.getSearchContent());
            }
        }
    }


}
