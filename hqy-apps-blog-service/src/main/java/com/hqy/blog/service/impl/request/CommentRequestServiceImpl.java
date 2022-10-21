package com.hqy.blog.service.impl.request;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.result.PageResult;
import com.hqy.blog.dto.ArticleCommentDTO;
import com.hqy.blog.entity.Comment;
import com.hqy.blog.service.ArticleCommentCompositeService;
import com.hqy.blog.service.request.CommentRequestService;
import com.hqy.blog.vo.AdminPageCommentsVO;
import com.hqy.blog.vo.ArticleCommentVO;
import com.hqy.blog.vo.ChildArticleCommentVO;
import com.hqy.blog.vo.ParentArticleCommentVO;
import com.hqy.web.service.account.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentRequestServiceImpl implements CommentRequestService {

    private final ArticleCommentCompositeService articleCommentCompositeService;

    @Override
    public DataResponse getArticlePageComments(Long articleId, Integer pageNumber, Integer pageSize) {
        PageResult<ArticleCommentVO> pageResult;
        // 先分页获取父级评论
        PageHelper.startPage(pageNumber, pageSize);
        List<Comment> parentComments = articleCommentCompositeService.commentTkService().selectParentComments(articleId);
        if (CollectionUtils.isEmpty(parentComments)) {
            pageResult = new PageResult<>();
        } else {
            // 获取分页结果
            PageInfo<Comment> pageInfo = new PageInfo<>(parentComments);
            // 获取父级评论ids
            List<Long> parents = parentComments.stream().map(Comment::getId).collect(Collectors.toList());
            // 获取子级评论.
            List<ArticleCommentDTO> childrenComments = articleCommentCompositeService.commentTkService().selectChildrenComments(parents, articleId);
            //构建分页返回结果集
            List<ArticleCommentVO> result = buildArticleCommentVo(parentComments, childrenComments);
            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), result);
        }
        return CommonResultCode.dataResponse(pageResult);
    }

    private List<ArticleCommentVO> buildArticleCommentVo(List<Comment> parentComments, List<ArticleCommentDTO> childrenComments) {
        //子级评论列表转换成MAP.
        Map<Long, List<Comment>> articleCommentMap;
        if (CollectionUtils.isEmpty(childrenComments)) {
            articleCommentMap = new HashMap<>(0);
        } else {
            articleCommentMap = childrenComments.stream().collect(Collectors.toMap(ArticleCommentDTO::getParent, ArticleCommentDTO::getComments));
        }

        //获取评论和被评论用户信息.
        Map<Long, ParentArticleCommentVO.User> commentReplierMap = getCommenterAndReplier(articleCommentMap, parentComments);
        //构建返回VO
        return parentComments.stream().map(comment -> {
            Long id = comment.getId();
            List<Comment> comments = articleCommentMap.get(id);
            List<ChildArticleCommentVO> childArticleCommentVOS = comments.stream()
                    .map(e -> new ChildArticleCommentVO(e, commentReplierMap.get(e.getCommenter()), commentReplierMap.get(e.getReplier()))).collect(Collectors.toList());
            return new ArticleCommentVO(comment, commentReplierMap.get(comment.getCommenter()), childArticleCommentVOS);
        }).collect(Collectors.toList());

    }

    private Map<Long, ParentArticleCommentVO.User> getCommenterAndReplier(Map<Long, List<Comment>> articleCommentMap, List<Comment> parentComments) {
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
        List<AccountBaseInfoStruct> accountBaseInfos =
                AccountRpcUtil.getAccountBaseInfos(accountIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(accountBaseInfos)) {
            return MapUtil.newHashMap(0);
        }
        return accountBaseInfos.stream().collect(Collectors.toMap(AccountBaseInfoStruct::getId, e -> new ParentArticleCommentVO.User(e.id.toString(), e.avatar, e.nickname)));
    }

    @Override
    public DataResponse getPageComments(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        PageResult<AdminPageCommentsVO> pageResultComments;
        List<Comment> comments = articleCommentCompositeService.commentTkService().queryAll();
        if (CollectionUtils.isEmpty(comments)) {
            pageResultComments = new PageResult<>();
        } else {
            PageInfo<Comment> pageInfo = new PageInfo<>(comments);
            Map<Long, AccountBaseInfoStruct> accountBaseInfoMap = AccountRpcUtil.getAccountBaseInfoMap(comments.stream().map(Comment::getCommenter).distinct().collect(Collectors.toList()));
            List<AdminPageCommentsVO> pageComments = comments.stream().map(comment -> convert(comment, accountBaseInfoMap)).collect(Collectors.toList());
            pageResultComments = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), pageComments);
        }
        return CommonResultCode.dataResponse(pageResultComments);
    }

    private AdminPageCommentsVO convert(Comment comment, Map<Long, AccountBaseInfoStruct> map) {
        return new AdminPageCommentsVO(comment.getId(), comment.getArticleId(), comment.getContent(), getShowName(comment.getCommenter(), map),
                getShowName(comment.getReplier(), map), DateUtil.date(comment.getCreated()).toString(), comment.getDeleted());
    }

    private String getShowName(Long id, Map<Long, AccountBaseInfoStruct> map) {
        AccountBaseInfoStruct struct = map.get(id);
        if (struct == null) {
            return StringConstants.EMPTY;
        }
        return StringUtils.isBlank(struct.nickname) ? struct.username : struct.nickname;
    }

}
