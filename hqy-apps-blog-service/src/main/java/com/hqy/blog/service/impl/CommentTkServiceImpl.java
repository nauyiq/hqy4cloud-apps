package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.CommentDao;
import com.hqy.blog.dto.ArticleCommentDTO;
import com.hqy.blog.entity.Comment;
import com.hqy.blog.service.CommentTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public BaseDao<Comment, Long> getTkDao() {
        return commentDao;
    }
}
