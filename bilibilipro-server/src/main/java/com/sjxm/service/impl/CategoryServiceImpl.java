package com.sjxm.service.impl;

import com.sjxm.constant.MessageConstant;
import com.sjxm.entity.Category;
import com.sjxm.entity.Video;
import com.sjxm.exception.CategoryNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.CategoryMapper;
import com.sjxm.service.CategoryService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.CategoryVO;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<CategoryVO> homecategory() {

        List<Category> list = categoryMapper.getHomeCategory();

        List<CategoryVO> result = new ArrayList<>();

        for (Category category : list) {
            CategoryVO categoryVO = info(category.getCategoryId());
            result.add(categoryVO);
        }

        return result;
    }

    /**
     * 获取分类信息
     * @param categoryId
     * @return
     */
    @Override
    public CategoryVO info(Long categoryId) {
        String redisKey = "category:categoryId="+categoryId;
        Object o = redisUtil.get(redisKey);
        if(o!=null){
            JacksonObjectMapper om = new JacksonObjectMapper();
            return om.convertValue(o, CategoryVO.class);
        }
        else{
            Category category = categoryMapper.getCategoryById(categoryId);
            if(category == null){
                throw new CategoryNotFoundException(MessageConstant.CATEGORY_NOT_FOUND);
            }
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(category,categoryVO);
            redisUtil.set(redisKey,categoryVO);
            return categoryVO;
        }
    }

    @Override
    public void update(CategoryVO categoryVO) {

        String redisKey = "category:categoryId="+categoryVO.getCategoryId();
        redisUtil.set(redisKey,categoryVO);

    }

    @Override
    public void transCategoryFromRedis2DB() {
        Set<String> keys = redisUtil.keys("category:categoryId=" + "*");
        JacksonObjectMapper om = new JacksonObjectMapper();
        if (keys != null) {
            for (String key : keys) {
                Object o = redisUtil.get(key);
                CategoryVO categoryVO = om.convertValue(o, CategoryVO.class);
                Category category = new Category();
                BeanUtils.copyProperties(categoryVO,category);
                categoryMapper.updateCategory(category);
                redisUtil.del(key);
            }
        }
    }
}
