package com.hqy.cloud.apps.blog.service.request.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.apps.blog.dto.ArticleCommentDTO;
import com.hqy.cloud.apps.blog.dto.PublishCommentDTO;
import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.entity.Comment;
import com.hqy.cloud.apps.blog.service.opeations.BlogDbOperationService;
import com.hqy.cloud.apps.blog.service.request.CommentRequestService;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsType;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsTypeHashCache;
import com.hqy.cloud.apps.blog.vo.AdminPageCommentsVO;
import com.hqy.cloud.apps.blog.vo.ArticleCommentVO;
import com.hqy.cloud.apps.blog.vo.ChildArticleCommentVO;
import com.hqy.cloud.apps.blog.vo.ParentArticleCommentVO;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.util.MathUtil;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Blog.DEFAULT_COMMENT_TABLE_COUNT;
import static com.hqy.cloud.apps.commom.result.AppsResultCode.ARTICLE_NOT_FOUND;
import static com.hqy.cloud.apps.commom.result.AppsResultCode.COMMENT_NOT_FOUND;
import static com.hqy.cloud.common.result.ResultCode.LIMITED_AUTHORITY;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentRequestServiceImpl implements CommentRequestService {

    private final BlogDbOperationService blogDbOperationService;
    private final StatisticsTypeHashCache<Long, StatisticsDTO> statisticsTypeHashCache;

    @Value("${spring.shardingsphere.sharding.tables.t_comment.count:4}")
    private int commentTableCount = DEFAULT_COMMENT_TABLE_COUNT;

    @Override
    public R<PageResult<AdminPageCommentsVO>> getPageComments(Long articleId, String content, Integer pageNumber, Integer pageSize) {
        PageInfo<Comment> pageInfo = blogDbOperationService.commentTkService().queryPageComments(articleId, content, pageNumber, pageSize);
        PageResult<AdminPageCommentsVO> pageResult;
        List<Comment> comments = pageInfo.getList();
        if (CollectionUtils.isEmpty(comments)) {
            pageResult = new PageResult<>();
        } else {
            Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(comments.stream().map(Comment::getCommenter).distinct().collect(Collectors.toList()));
            List<AdminPageCommentsVO> pageComments = comments.stream().map(comment -> convert(comment, profileMap)).collect(Collectors.toList());
            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), pageComments);
        }
        return R.ok(pageResult);
    }

    @Override
    public R<PageResult<ParentArticleCommentVO>> getArticlePageComments(Long articleId, Integer pageNumber, Integer pageSize) {
        PageResult<ParentArticleCommentVO> pageResult;
        // 先分页获取父级评论
        PageHelper.startPage(pageNumber, pageSize);
        List<Comment> parentComments = blogDbOperationService.commentTkService().selectParentComments(articleId);
        if (CollectionUtils.isEmpty(parentComments)) {
            pageResult = new PageResult<>();
        } else {
            // 获取分页结果
            PageInfo<Comment> pageInfo = new PageInfo<>(parentComments);
            // 获取父级评论ids
            List<Long> parents = parentComments.stream().map(Comment::getId).collect(Collectors.toList());
            // 获取子级评论.
            List<ArticleCommentDTO> childrenComments = blogDbOperationService.commentTkService().selectChildrenComments(parents, articleId);
            //构建分页返回结果集
            List<ParentArticleCommentVO> result = buildArticleCommentVo(parentComments, childrenComments);
            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), result);
        }
        return R.ok(pageResult);
    }

    private List<ParentArticleCommentVO> buildArticleCommentVo(List<Comment> parentComments, List<ArticleCommentDTO> childrenComments) {
        //子级评论列表转换成MAP.
        Map<Long, List<Comment>> articleCommentMap;
        if (CollectionUtils.isEmpty(childrenComments)) {
            articleCommentMap = new HashMap<>(0);
        } else {
            articleCommentMap = childrenComments.stream().collect(Collectors.toMap(ArticleCommentDTO::getParent, ArticleCommentDTO::getComments));
        }

        //获取评论和被评论用户信息.
        Map<Long, ArticleCommentVO.User> commentReplierMap = getCommenterAndReplier(articleCommentMap, parentComments);
        //构建返回VO
        return parentComments.stream().map(comment -> {
            Long id = comment.getId();
            List<Comment> comments = articleCommentMap.get(id);
            if (CollectionUtils.isEmpty(comments)) {
                return new ParentArticleCommentVO(comment, commentReplierMap.get(comment.getCommenter()), new ArrayList<>());
            }
            List<ChildArticleCommentVO> childArticleComments = comments.stream()
                    .map(e -> new ChildArticleCommentVO(e, commentReplierMap.get(e.getCommenter()), commentReplierMap.get(e.getReplier()))).collect(Collectors.toList());
            return new ParentArticleCommentVO(comment, commentReplierMap.get(comment.getCommenter()), childArticleComments);
        }).collect(Collectors.toList());

    }

    private Map<Long, ArticleCommentVO.User> getCommenterAndReplier(Map<Long, List<Comment>> articleCommentMap, List<Comment> parentComments) {
        // 获取遍历map和list所有需要用的账户id.
        List<Long> accountIds = parentComments.stream().map(Comment::getCommenter).collect(Collectors.toList());
        Collection<List<Comment>> values = articleCommentMap.values();
        for (List<Comment> comments : values) {
            if (CollectionUtils.isNotEmpty(comments)) {
                for (Comment comment : comments) {
                    accountIds.add(comment.getCommenter());
                    accountIds.add(comment.getReplier());
                }
            }
        }
        //账号RPC 获取用户信息
        List<AccountProfileStruct> accountBaseInfos =
                AccountRpcUtil.getAccountProfiles(accountIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(accountBaseInfos)) {
            return MapUtil.newHashMap(0);
        }
        return accountBaseInfos.stream().collect(Collectors.toMap(AccountProfileStruct::getId, e -> new ArticleCommentVO.User(e.id.toString(), e.avatar, e.nickname)));
    }


    @Override
    public R<Boolean> publishComment(PublishCommentDTO publishComment, Long accessAccountId) {
        long articleId = Long.parseLong(publishComment.getArticleId());
        Article article = blogDbOperationService.articleTkService().queryById(articleId);
        if (Objects.isNull(article)) {
            return R.failed(ARTICLE_NOT_FOUND);
        }
        // 入库
//        long id = genCommentId(articleId);
        Comment comment = new Comment(null, articleId, accessAccountId, Convert.toLong(publishComment.getReplier()),
                publishComment.getContent(), publishComment.getLevel(), Convert.toLong(publishComment.getParentId()));
        if (blogDbOperationService.commentTkService().manualInsert(comment) <= 0) {
            return R.failed();
        }
        // 评论数 + 1
        statisticsTypeHashCache.increment(articleId, StatisticsType.COMMENTS, 1);
        return R.ok();
    }

    @Override
    public R<Boolean> deleteComment(Long accessAccountId, Long commentId) {
        Comment comment = blogDbOperationService.commentTkService().queryById(commentId);
        if (Objects.isNull(comment)) {
            return R.failed(COMMENT_NOT_FOUND);
        }

        if (Objects.nonNull(accessAccountId) && !comment.getCommenter().equals(accessAccountId)) {
            return R.failed(LIMITED_AUTHORITY);
        }
        // 修改库
        comment.setDeleted(true);
        if (!blogDbOperationService.commentTkService().update(comment)) {
            return R.failed();
        }
        // 评论数 - 1
        statisticsTypeHashCache.increment(comment.getArticleId(), StatisticsType.COMMENTS, -1);
        return R.ok();
    }

    private long genCommentId(Long articleId) {
        // 获取雪花id
        long snowflakeId = DistributedIdGen.getSnowflakeId(MicroServiceConstants.BLOG_SERVICE);
        // 根据文章id进行获取基因后缀
        String fetchGene = MathUtil.fetchGene(articleId, commentTableCount);
        return MathUtil.newIdWithGene(snowflakeId, fetchGene);
    }



    private AdminPageCommentsVO convert(Comment comment, Map<Long, AccountProfileStruct> map) {
        return new AdminPageCommentsVO(comment.getId().toString(), comment.getArticleId().toString(), comment.getContent(), getShowName(comment.getCommenter(), map),
                getShowName(comment.getReplier(), map), comment.getLevel() ,DateUtil.date(comment.getCreated()).toString(), comment.getDeleted());
    }

    private String getShowName(Long id, Map<Long, AccountProfileStruct> map) {
        AccountProfileStruct struct = map.get(id);
        if (struct == null) {
            return StringConstants.EMPTY;
        }
        return StringUtils.isBlank(struct.nickname) ? struct.username : struct.nickname;
    }

}
