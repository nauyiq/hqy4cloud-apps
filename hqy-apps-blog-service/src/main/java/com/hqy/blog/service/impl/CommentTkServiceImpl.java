package com.hqy.blog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.base.BaseDao;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.CommentDao;
import com.hqy.blog.dto.ArticleCommentDTO;
import com.hqy.blog.entity.Comment;
import com.hqy.blog.service.CommentTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.base.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentTkServiceImpl extends BaseTkServiceImpl<Comment, Long> implements CommentTkService {

    private final CommentDao commentDao;

    @Override
    public List<Comment> selectParentComments(Long articleId) {
        if (articleId == null) {
            return Collections.emptyList();
        }
        return commentDao.selectParentComments(articleId);
    }

    @Override
    public List<ArticleCommentDTO> selectChildrenComments(List<Long> parents, Long articleId) {
        if (CollectionUtils.isEmpty(parents) || articleId == null) {
            return Collections.emptyList();
        }
        return commentDao.selectChildrenComments(parents, articleId);
    }

    @Override
    public PageInfo<Comment> queryPageComments(Long articleId, String content, Integer pageNumber, Integer pageSize) {
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", false);
        if (Objects.nonNull(articleId)) {
            criteria.andLike("articleId", PERCENT + articleId + PERCENT);
        }
        if (StringUtils.isNotBlank(content)) {
            criteria.andLike("content", PERCENT + content + PERCENT);
        }
        PageHelper.startPage(pageNumber, pageSize);
        List<Comment> comments = commentDao.selectByExample(example);
        if (CollectionUtils.isEmpty(comments)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(comments);
    }

    @Override
    public List<Comment> queryCommentsByArticleIds(List<Long> articleIds) {
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", false)
                .andIn("articleId", articleIds);
        return commentDao.selectByExample(example);
    }

    @Override
    public boolean deleteComments(List<Comment> comments) {
        List<Long> ids = comments.stream().map(Comment::getId).collect(Collectors.toList());
        return commentDao.deleteComments(ids) > 0;
    }

    @Override
    public BaseDao<Comment, Long> getTkDao() {
        return commentDao;
    }
}
