package com.sjxm.service.impl;

import com.sjxm.constant.MessageConstant;
import com.sjxm.context.BaseContext;
import com.sjxm.dto.ArticleDTO;
import com.sjxm.dto.ArticleQueryDTO;
import com.sjxm.entity.Article;
import com.sjxm.exception.ArticleNotFoundException;
import com.sjxm.mapper.ArticleMapper;
import com.sjxm.service.AccountService;
import com.sjxm.service.ArticleService;
import com.sjxm.service.CategoryService;
import com.sjxm.vo.ArticleVO;
import com.sjxm.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountService accountService;

    /**
     * 添加文章
     * @param articleDTO
     * @return
     */
    @Override
    public Long add(ArticleDTO articleDTO) {
        List<Long> tags = articleDTO.getTags();
        Article article = Article.builder()
                .title(articleDTO.getTitle())
                .poster(articleDTO.getPoster())
                .brief(articleDTO.getBrief())
                .content(articleDTO.getContent())
                .category(articleDTO.getCategory())
                .postTime(LocalDateTime.now())
                .uid(BaseContext.getUID())
                .build();
        if(tags!=null&&tags.size()>0){
            StringJoiner joiner = new StringJoiner(",");
            for (long value : tags) {
                joiner.add(String.valueOf(value));
            }

            String newTags = joiner.toString();
            article.setTags(newTags);
        }
        articleMapper.add(article);

        return article.getArticleId();
    }

    @Override
    public List<ArticleVO> list(ArticleQueryDTO articleQueryDTO) {

        Integer category = articleQueryDTO.getCategory();
        Long uid = articleQueryDTO.getUid();
        Integer chosedTime = articleQueryDTO.getChosedTime();
        String title = articleQueryDTO.getTitle();
        Integer chosedOrder = articleQueryDTO.getChosedOrder();

        List<Article> list = articleMapper.list(category,uid,chosedTime,chosedOrder,title);

        List<ArticleVO> result = new ArrayList<>();

        for (Article article : list) {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article,articleVO);
            String tags = article.getTags();
            List<CategoryVO> categoryTags = new ArrayList<>();
            if(tags!=null){
                String[] tagArray = tags.split(",");
                for (String str : tagArray) {
                    categoryTags.add(categoryService.info(Long.valueOf(str)));
                }
            }
            articleVO.setTags(categoryTags);
            articleVO.setAccountVO(accountService.info(articleVO.getUid()));
            result.add(articleVO);
        }

        return result;
    }

    /**
     * 获取具体文章信息
     * @param articleId
     * @return
     */
    @Override
    public ArticleVO get(Long articleId) {

        Article article =  articleMapper.getArticleByArticleId(articleId);

        if(article == null){
            throw new ArticleNotFoundException(MessageConstant.MESSAGE_NOT_FOUND);
        }

        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article,articleVO);
        String tags = article.getTags();
        List<CategoryVO> categoryTags = new ArrayList<>();
        if(tags!=null){
            String[] tagArray = tags.split(",");
            for (String str : tagArray) {
                categoryTags.add(categoryService.info(Long.valueOf(str)));
            }
        }
        articleVO.setTags(categoryTags);
        articleVO.setAccountVO(accountService.info(articleVO.getUid()));

        return articleVO;
    }
}
