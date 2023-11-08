package com.hqy.cloud.apps.blog.service.request.impl;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.apps.blog.converter.TypeConverter;
import com.hqy.cloud.apps.blog.dto.TypeDTO;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.entity.Comment;
import com.hqy.cloud.apps.blog.entity.Type;
import com.hqy.cloud.apps.blog.service.opeations.BlogDbOperationService;
import com.hqy.cloud.apps.blog.service.request.ArticleTypeRequestService;
import com.hqy.cloud.apps.blog.vo.ArticleTypeVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.result.AppsResultCode.NOT_FOUND_TYPE;
import static com.hqy.cloud.apps.commom.result.AppsResultCode.TYPE_EXIST;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:54
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTypeRequestServiceImpl implements ArticleTypeRequestService {

    private final BlogDbOperationService service;
    private final TransactionTemplate transactionTemplate;

    @Override
    public R<PageResult<ArticleTypeVO>> adminPageTypes(String name, Integer current, Integer size) {
        PageResult<ArticleTypeVO> pageResult;
        PageInfo<Type> pageInfo = service.typeTkService().queryPageTypes(name, current, size);
        List<Type> types = pageInfo.getList();
        if (CollectionUtils.isEmpty(types)) {
            pageResult = new PageResult<>();
        } else {
            List<ArticleTypeVO> typeVOList = types.stream().map(TypeConverter.CONVERTER::convert).collect(Collectors.toList());
            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), typeVOList);
        }
        return R.ok(pageResult);
    }

    @Override
    public R<List<ArticleTypeVO>> enableArticleTypes() {
        List<Type> allTypes = service.typeTkService().queryList(new Type(true));
        List<ArticleTypeVO> result;
        if (CollectionUtils.isEmpty(allTypes)) {
            result = Collections.emptyList();
        } else {
            result = allTypes.stream().map(TypeConverter.CONVERTER::convert).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    @Override
    public R<Boolean> addType(TypeDTO typeDTO) {
        String name = typeDTO.getName();
        Type type = service.typeTkService().queryOne(new Type(name));
        if (Objects.nonNull(type)) {
            return R.failed(TYPE_EXIST);
        }
        type = TypeConverter.CONVERTER.convert(typeDTO);
        return service.typeTkService().insert(type) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editType(TypeDTO typeDTO) {
        Type type = service.typeTkService().queryById(typeDTO.getId());
        if (Objects.isNull(type)) {
            return R.failed(NOT_FOUND_TYPE);
        }
        // check name exist.
        if (!typeDTO.getName().equals(type.getName())) {
            Type byName = service.typeTkService().queryOne(new Type(typeDTO.getName()));
            if (byName != null) {
                return R.failed(TYPE_EXIST);
            }
        }
        //update.
        TypeConverter.CONVERTER.updateByDTO(typeDTO, type);
        return service.typeTkService().update(type) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteType(Integer id) {
        Type type = service.typeTkService().queryById(id);
        if (Objects.isNull(type)) {
            return R.failed(NOT_FOUND_TYPE);
        }
        List<Article> articles = service.articleTkService().queryList(new Article(id));
        List<Comment> comments = queryByArticles(articles);

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                type.setDeleted(true);
                AssertUtil.isTrue(service.typeTkService().update(type), "Failed execute to update type.");
                if (CollectionUtils.isNotEmpty(articles)) {
                    AssertUtil.isTrue(service.articleTkService().deleteArticles(articles), "Failed execute to delete articles.");
                }
                if (CollectionUtils.isNotEmpty(comments)) {
                    AssertUtil.isTrue(service.commentTkService().deleteComments(comments), "Failed execute to delete comments.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage());
                return false;
            }
        });

        return Boolean.TRUE.equals(execute) ? R.ok() : R.failed();
    }

    private List<Comment> queryByArticles(List<Article> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            return null;
        }
        List<Long> articleIds = articles.stream().map(Article::getId).collect(Collectors.toList());
        return service.commentTkService().queryCommentsByArticleIds(articleIds);
    }
}
