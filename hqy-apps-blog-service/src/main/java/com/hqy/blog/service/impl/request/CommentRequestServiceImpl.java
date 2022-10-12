package com.hqy.blog.service.impl.request;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.result.PageResult;
import com.hqy.blog.entity.Comment;
import com.hqy.blog.service.ArticleCommentCompositeService;
import com.hqy.blog.service.request.CommentRequestService;
import com.hqy.blog.vo.AdminPageCommentsVO;
import com.hqy.web.service.account.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    public DataResponse getPageComments(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        PageResult<AdminPageCommentsVO> pageResultComments;
        List<Comment> comments = articleCommentCompositeService.commentTkService().queryAll();
        if (CollectionUtils.isEmpty(comments)) {
            pageResultComments = new PageResult<>();
        } else {
            PageInfo<Comment> pageInfo = new PageInfo<>(comments);
            Map<Long, AccountBaseInfoStruct> accountBaseInfoMap = AccountRpcUtil.getAccountBaseInfoMap(comments.stream().map(Comment::getCommentId).distinct().collect(Collectors.toList()));
            List<AdminPageCommentsVO> pageComments = comments.stream().map(comment -> convert(comment, accountBaseInfoMap)).collect(Collectors.toList());
            pageResultComments = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), pageComments);
        }
        return CommonResultCode.dataResponse(pageResultComments);
    }

    private AdminPageCommentsVO convert(Comment comment, Map<Long, AccountBaseInfoStruct> map) {
        return new AdminPageCommentsVO(comment.getId(), comment.getArticleId(), comment.getContent(), getShowName(comment.getCommentId(), map),
                getShowName(comment.getReplyId(), map), comment.getStatus(), DateUtil.date(comment.getCreated()).toString());
    }

    private String getShowName(Long id, Map<Long, AccountBaseInfoStruct> map) {
        AccountBaseInfoStruct struct = map.get(id);
        if (struct == null) {
            return StringConstants.EMPTY;
        }
        return StringUtils.isBlank(struct.nickname) ? struct.username : struct.nickname;
    }

}
