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
import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.apps.blog.es.service.ArticleElasticService;
import com.hqy.cloud.apps.blog.service.BlogDbOperationService;
import com.hqy.cloud.apps.blog.service.request.ArticleRequestService;
import com.hqy.cloud.apps.blog.service.statistics.ArticleStatisticsServer;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsType;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsTypeHashCache;
import com.hqy.cloud.apps.blog.vo.ArticleDetailVO;
import com.hqy.cloud.apps.blog.vo.PageArticleVO;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.ParentExecutorService;
import com.hqy.cloud.web.common.AccountRpcUtil;
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
    private final ArticleStatisticsServer articleStatisticsServer;
    private final BlogDbOperationService blogDbOperationService;
    private final ArticleElasticService articleElasticService;
    private final StatisticsTypeHashCache<Long, StatisticsDTO> statisticsTypeHashCache;

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
        boolean result = blogDbOperationService.articleTkService().insert(article);
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editArticle(ArticleDTO articleDTO) {
        if (!checkTypeExist(articleDTO.getType())) {
            return R.failed(INVALID_ARTICLE_TYPE);
        }
        Article article = blogDbOperationService.articleTkService().queryNotContentById(articleDTO.getId());
        if (Objects.isNull(article)) {
            return R.failed(INVALID_ARTICLE_ID);
        }

        //更新db
        ArticleConverter.CONVERTER.updateByDTO(articleDTO, article);
        boolean update = blogDbOperationService.articleTkService().update(article);
        return update ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteArticle(Long id) {
        Article article = blogDbOperationService.articleTkService().queryNotContentById(id);
        if (Objects.isNull(article)) {
            return R.failed(INVALID_ARTICLE_ID);
        }
        boolean result = blogDbOperationService.deleteArticle(article);
        return result? R.ok() : R.failed();
    }

    @Override
    public R<PageResult<PageArticleVO>> adminPageArticles(String title, String describe, Integer current, Integer size) {
        // 优先从ES中取出.
        PageResult<ArticleDoc> result = articleElasticService.queryPage(title, describe, current, size);
        List<ArticleDoc> resultList = result.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            // es为空时尝试从db去取
            PageInfo<Article> pageInfo = blogDbOperationService.articleTkService().pageArticles(title, describe, current, size);
            if (CollectionUtils.isEmpty(pageInfo.getList())) {
                return R.ok(new PageResult<>());
            }
            resultList = pageInfo.getList().stream().map(ArticleConverter.CONVERTER::convertDoc).collect(Collectors.toList());
            result = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), resultList);
        }

        //转换VO
        Map<Integer, Type> typesMap = getTypesMap();
        List<PageArticleVO> articleVoList = resultList.stream().map(ArticleConverter.CONVERTER::convert)
                .peek(vo -> settingTypeName(vo, typesMap)).collect(Collectors.toList());
        PageResult<PageArticleVO> pageResult = new PageResult<>(result.getCurrentPage(), result.getTotal(), result.getPages(), articleVoList);
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
            //获取每个文章的统计数据.
            List<StatisticsDTO> statistics = articleStatisticsServer.getStatistics(pageArticles.stream().map(PageArticleDTO::getId).collect(Collectors.toList()));
            Map<Long, StatisticsDTO> map = statistics.stream().collect(Collectors.toMap(StatisticsDTO::getId, e -> e));
            PageInfo<PageArticleDTO> pageInfo = new PageInfo<>(pageArticles);
            List<PageArticleVO> articleVOList = pageArticles.stream().map(e -> new PageArticleVO(e, map.get(e.getId()))).collect(Collectors.toList());
            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), articleVOList);
        }

        return R.ok(pageResult);
    }

    @Override
    public R<ArticleDetailVO> articleDetail(Long accessAccountId, Long id) {
        Optional<ArticleDoc> optional = articleElasticService.findById(id);
        ArticleDoc articleDoc = optional.orElseGet(() -> {
            Article article = blogDbOperationService.articleTkService().queryById(id);
            if (Objects.isNull(article)) {
                return null;
            }
            ArticleDoc doc = ArticleConverter.CONVERTER.convertDoc(article);
            ParentExecutorService.getInstance().execute(() -> articleElasticService.save(doc));
            return doc;
        });

        if (Objects.isNull(articleDoc)) {
            return R.failed(AppsResultCode.ARTICLE_NOT_FOUND);
        }

        //获取Article Author NAME.
        Long author = articleDoc.getAuthor();
        AccountBaseInfoStruct accountBaseInfo = AccountRpcUtil.getAccountBaseInfo(author);
        String authorName = accountBaseInfo == null ? StringConstants.EMPTY : accountBaseInfo.nickname;

        //获取当前文章的统计数据.
        StatisticsDTO statistics = articleStatisticsServer.getStatistics(id);
        //获取当前用户的状态对文章当前文章的统计状态 -> 是否点赞等 | 是否已读.
        AccountAccessArticleStatusDTO status = getAccessArticleStatus(accessAccountId, id);
        //Build Article VO
        ArticleDetailVO articleDetail = new ArticleDetailVO(authorName, articleDoc, statistics, status);
        return R.ok(articleDetail);
    }

    @Override
    public R<Boolean> articleLiked(Long accessAccountId, Long articleId) {
        Article article = blogDbOperationService.articleTkService().queryById(articleId);
        if (Objects.isNull(article)) {
            return R.failed(AppsResultCode.ARTICLE_NOT_FOUND);
        }

        if (articleStatisticsServer.updateStatus(accessAccountId, StatisticsType.LIKES, articleId)) {
            // 走消息队列异步写表或者直接入库写表
            return R.ok();
        } else {
            return R.failed();
        }
    }

    @Override
    public R<Boolean> articleRead(Long articleId, Long accountId) {
        if (Objects.nonNull(accountId)) {
            return articleStatisticsServer.updateStatus(accountId, StatisticsType.VISITS, articleId) ? R.ok() : R.failed();
        } else {
            statisticsTypeHashCache.incrAndGet(articleId, StatisticsType.VISITS, 1);
            return R.ok();
        }
    }

    private AccountAccessArticleStatusDTO getAccessArticleStatus(Long accessAccountId, Long articleId) {
        AccountAccessArticleStatusDTO status;
        if (accessAccountId == null) {
            status = new AccountAccessArticleStatusDTO(true, false);
        } else {
            status = articleStatisticsServer.status(accessAccountId, articleId);
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
