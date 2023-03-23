package com.hqy.cloud.apps.blog.service.impl.request;

import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.apps.blog.converter.ArticleConverter;
import com.hqy.cloud.apps.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.cloud.apps.blog.dto.ArticleDTO;
import com.hqy.cloud.apps.blog.dto.PageArticleDTO;
import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.entity.Liked;
import com.hqy.cloud.apps.blog.entity.Type;
import com.hqy.cloud.apps.blog.service.BlogDbOperationService;
import com.hqy.cloud.apps.blog.service.request.ArticleRequestService;
import com.hqy.cloud.apps.blog.statistics.AccountAccessArticleServer;
import com.hqy.cloud.apps.blog.statistics.StatisticsRedisService;
import com.hqy.cloud.apps.blog.statistics.StatisticsType;
import com.hqy.cloud.apps.blog.vo.ArticleDetailVO;
import com.hqy.cloud.apps.blog.vo.PageArticleVO;
import com.hqy.cloud.apps.blog.vo.StatisticsVO;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.ParentExecutorService;
import com.hqy.web.service.account.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.result.AppsResultCode.INVALID_ARTICLE_ID;
import static com.hqy.cloud.apps.commom.result.AppsResultCode.INVALID_ARTICLE_TYPE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleRequestServiceImpl implements ArticleRequestService {

    private final StatisticsRedisService<Long, StatisticsDTO> statisticsRedisService;
    private final AccountAccessArticleServer accountAccessArticleServer;
    private final BlogDbOperationService blogDbOperationService;

    @Override
    public R<Boolean> publishArticle(ArticleDTO articleDTO) {
        boolean exist = checkTypeExist(articleDTO.getType());
        if (!exist) {
            return R.failed(INVALID_ARTICLE_TYPE);
        }
        //insert article to db.
        long id = DistributedIdGen.getSnowflakeId();
        Date date = new Date();
        Article article = new Article(id, articleDTO.getTitle(), articleDTO.getDescription(), articleDTO.getCover(), articleDTO.getContent(), articleDTO.getType(),
                articleDTO.getMusicUrl(), articleDTO.getMusicName(), articleDTO.getAuthor(), articleDTO.getStatus(), date);
        if (!blogDbOperationService.articleTkService().insert(article)) {
            return R.failed();
        }
        return R.ok();
    }

    @Override
    public R<Boolean> editArticle(ArticleDTO articleDTO) {
        if (!checkTypeExist(articleDTO.getType())) {
            return R.failed(INVALID_ARTICLE_TYPE);
        }
        Article article = blogDbOperationService.articleTkService().queryById(articleDTO.getId());
        if (Objects.isNull(article)) {
            return R.failed(INVALID_ARTICLE_ID);
        }
        ArticleConverter.CONVERTER.updateByDTO(articleDTO, article);
        return blogDbOperationService.articleTkService().update(article) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteArticle(Long id) {
        Article article = blogDbOperationService.articleTkService().queryById(id);
        if (Objects.isNull(article)) {
            return R.failed(INVALID_ARTICLE_ID);
        }
        return blogDbOperationService.deleteArticle(article) ? R.ok() : R.failed();
    }

    @Override
    public R<PageResult<PageArticleVO>> adminPageArticles(String title, String describe, Integer current, Integer size) {
        PageInfo<Article> pageInfo = blogDbOperationService.articleTkService().pageArticles(title, describe, current, size);
        List<Article> articles = pageInfo.getList();
        if (CollectionUtils.isEmpty(articles)) {
            return R.ok(new PageResult<>());
        }
        Map<Integer, Type> typesMap = getTypesMap();
        List<PageArticleVO> articleVOS = articles.stream().map(ArticleConverter.CONVERTER::convert)
                .peek(vo -> settingTypeName(vo, typesMap)).collect(Collectors.toList());
        PageResult<PageArticleVO> pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), articleVOS);
        return R.ok(pageResult);
    }

    private void settingTypeName(PageArticleVO vo, Map<Integer, Type> typesMap) {
        Type type = typesMap.get(vo.getType());
        AssertUtil.notNull(type, "Type should not be null.");
        vo.setTypeName(type.getName());
    }

    private Map<Integer, Type> getTypesMap() {
        List<Type> types = blogDbOperationService.typeTkService().queryList(new Type());
        if (CollectionUtils.isEmpty(types)) {
            return MapUtil.newHashMap(0);
        } else {
            return types.stream().collect(Collectors.toMap(Type::getId, type -> type));
        }
    }

    @Override
    public R<PageResult<PageArticleVO>> pageArticles(Integer type, Integer pageNumber, Integer pageSize, Integer status) {
        PageHelper.startPage(pageNumber, pageSize);
        PageResult<PageArticleVO> pageResult;
        List<PageArticleDTO> pageArticles = blogDbOperationService.articleTkService().articles(type, status);
        if (CollectionUtils.isEmpty(pageArticles)) {
            pageResult = new PageResult<>();
        } else {
            PageInfo<PageArticleDTO> pageInfo = new PageInfo<>(pageArticles);
            List<PageArticleVO> articleVOList = pageArticles.stream().map(PageArticleVO::new).collect(Collectors.toList());
            List<PageArticleVO> resultList = new ArrayList<>(articleVOList.size());
            //获取每个文章的统计数据.
            List<StatisticsDTO> statistics = statisticsRedisService.getStatistics(articleVOList.stream().map(e -> Long.parseLong(e.getId())).collect(Collectors.toList()));
            for (int i = 0; i < articleVOList.size(); i++) {
                PageArticleVO pageArticleVO = articleVOList.get(i);
                StatisticsDTO statisticsDTO = statistics.get(i);
                pageArticleVO.setStatistics(new StatisticsVO(statisticsDTO.getVisits(), statisticsDTO.getLikes(), statisticsDTO.getComments()));
                resultList.add(i, pageArticleVO);
            }

            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), resultList);
        }

        return R.ok(pageResult);
    }

    @Override
    public  R<ArticleDetailVO> articleDetail(Long accessAccountId, Long id) {
        Article article = blogDbOperationService.articleTkService().queryById(id);
        if (Objects.isNull(article)) {
            return R.failed(AppsResultCode.INVALID_ARTICLE_ID);
        }
        //获取Article Author NAME.
        Long author = article.getAuthor();
        AccountBaseInfoStruct accountBaseInfo = AccountRpcUtil.getAccountBaseInfo(author);
        String authorName = accountBaseInfo == null ? StringConstants.EMPTY : accountBaseInfo.nickname;

        //获取当前文章的统计数据.
        StatisticsDTO statistics = statisticsRedisService.getStatistics(id);
        //获取当前用户的状态对文章当前文章的统计状态 -> 是否点赞等 | 是否已读.
        AccountAccessArticleStatusDTO status = getAccessArticleStatus(accessAccountId, id);
        //Build Article VO
        ArticleDetailVO articleDetail = new ArticleDetailVO(authorName, article, statistics, status);
        return R.ok(articleDetail);
    }

    @Override
    public R<Boolean> articleLiked(Long accessAccountId, Long articleId) {
        Article article = blogDbOperationService.articleTkService().queryById(articleId);
        if (Objects.isNull(article)) {
            return R.failed(AppsResultCode.ARTICLE_NOT_FOUND);
        }

        boolean status = accountAccessArticleServer.accessStatus(accessAccountId, StatisticsType.LIKES, articleId);
        //修改点赞状态.
        if (accountAccessArticleServer.changeAccessStatus(accessAccountId, StatisticsType.LIKES, articleId, !status) ) {
            // 修改统计数据
            statisticsRedisService.incrValue(articleId, StatisticsType.LIKES, status ? -1 : 1);
            // 走消息队列异步写表或者直接入库写表
            // 这里直接入库.
            // 点赞数据不关心数据一致性. 只需redis数据定时回写到db即可.
            ParentExecutorService.getInstance().execute(() -> blogDbOperationService.likedTkService().insertOrUpdate(new Liked(articleId, accessAccountId, !status)));
            return R.ok();
        } else {
            return R.failed();
        }
    }

    @Override
    public R<Boolean> articleRead(Long articleId) {
        statisticsRedisService.incrValue(articleId, StatisticsType.VISITS, 1);
        return R.ok();
    }

    private AccountAccessArticleStatusDTO getAccessArticleStatus(Long accessAccountId, Long articleId) {
        AccountAccessArticleStatusDTO status;
        if (accessAccountId == null) {
            status = new AccountAccessArticleStatusDTO(false, false);
        } else {
            status = accountAccessArticleServer.accessStatus(accessAccountId, articleId);
        }
        return status;
    }

    private boolean checkTypeExist(Integer type) {
        if (type == null) {
            return false;
        }
        return blogDbOperationService.typeTkService().queryById(type) != null;
    }


}
