package com.hqy.blog.service.impl.request;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.result.PageResult;
import com.hqy.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.blog.dto.ArticleDTO;
import com.hqy.blog.dto.PageArticleDTO;
import com.hqy.blog.dto.StatisticsDTO;
import com.hqy.blog.entity.Article;
import com.hqy.blog.service.ArticleCommentCompositeService;
import com.hqy.blog.service.ArticleTypeTagsCompositeService;
import com.hqy.blog.service.request.ArticleRequestService;
import com.hqy.blog.statistics.AccountAccessArticleServer;
import com.hqy.blog.statistics.StatisticsRedisService;
import com.hqy.blog.vo.ArticleDetailVO;
import com.hqy.blog.vo.PageArticleVO;
import com.hqy.blog.vo.StatisticsVO;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import com.hqy.web.service.account.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:47
 */
@Service
@RequiredArgsConstructor
public class ArticleRequestServiceImpl implements ArticleRequestService {

    private final StatisticsRedisService<Long, StatisticsDTO> statisticsRedisService;
    private final AccountAccessArticleServer accountAccessArticleServer;
    private final ArticleCommentCompositeService articleCommentCompositeService;
    private final ArticleTypeTagsCompositeService articleTypeTagsCompositeService;


    @Override
    public DataResponse AdminArticleRequestService(ArticleDTO articleDTO) {
        boolean exist = checkTypeExist(articleDTO.getType());
        if (!exist) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_TYPE);
        }
        //insert article to db.
        long id = ProjectSnowflakeIdWorker.getInstance().nextId();
        Date date = new Date();
        Article article = new Article(id, articleDTO.getTitle(), articleDTO.getDescription(), articleDTO.getCover(), articleDTO.getContent(), articleDTO.getType(),
                articleDTO.getMusicUrl(), articleDTO.getMusicName(), articleDTO.getAuthor(), true, date);
        if (!articleTypeTagsCompositeService.articleTkService().insert(article)) {
            return CommonResultCode.dataResponse(CommonResultCode.SYSTEM_ERROR_INSERT_FAIL);
        }

        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse pageArticles(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        PageResult<PageArticleVO> pageResult;
        List<PageArticleDTO> pageArticles = articleCommentCompositeService.articleTkService().articles();
        if (CollectionUtils.isEmpty(pageArticles)) {
            pageResult = new PageResult<>();
        } else {
            PageInfo<PageArticleDTO> pageInfo = new PageInfo<>(pageArticles);
            List<PageArticleVO> articleVOS = pageArticles.stream().map(PageArticleVO::new).collect(Collectors.toList());
            List<PageArticleVO> resultList = new ArrayList<>(articleVOS.size());
            //获取每个文章的统计数据.
            List<StatisticsDTO> statistics = statisticsRedisService.getStatistics(articleVOS.stream().map(e -> Long.parseLong(e.getId())).collect(Collectors.toList()));
            for (int i = 0; i < articleVOS.size(); i++) {
                PageArticleVO pageArticleVO = articleVOS.get(i);
                StatisticsDTO statisticsDTO = statistics.get(i);
                pageArticleVO.setStatistics(new StatisticsVO(statisticsDTO.getVisits(), statisticsDTO.getLikes(), statisticsDTO.getComments()));
                resultList.add(i, pageArticleVO);
            }

            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), resultList);
        }

        return CommonResultCode.dataResponse(pageResult);
    }

    @Override
    public DataResponse articleDetail(Long accessAccountId, Long id) {
        Article article = articleCommentCompositeService.articleTkService().queryById(id);
        if (Objects.isNull(article)) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_ID);
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
        return CommonResultCode.dataResponse(articleDetail);
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
        return articleTypeTagsCompositeService.typeTkService().queryById(type) != null;
    }


}
