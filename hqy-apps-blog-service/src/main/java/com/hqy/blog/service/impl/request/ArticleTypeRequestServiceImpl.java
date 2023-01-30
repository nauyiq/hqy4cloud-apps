package com.hqy.blog.service.impl.request;

import com.github.pagehelper.PageInfo;
import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.result.PageResult;
import com.hqy.blog.converter.TypeConverter;
import com.hqy.blog.dto.TypeDTO;
import com.hqy.blog.entity.Article;
import com.hqy.blog.entity.Comment;
import com.hqy.blog.entity.Type;
import com.hqy.blog.service.BlogDbOperationService;
import com.hqy.blog.service.request.ArticleTypeRequestService;
import com.hqy.blog.vo.ArticleTypeVO;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.apps.common.result.BlogResultCode.NOT_FOUND_TYPE;
import static com.hqy.apps.common.result.BlogResultCode.TYPE_EXIST;
import static com.hqy.base.common.result.CommonResultCode.SYSTEM_BUSY;

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
    public DataResponse adminPageTypes(String name, Integer current, Integer size) {
        PageResult<ArticleTypeVO> pageResult;
        PageInfo<Type> pageInfo = service.typeTkService().queryPageTypes(name, current, size);
        List<Type> types = pageInfo.getList();
        if (CollectionUtils.isEmpty(types)) {
            pageResult = new PageResult<>();
        } else {
            List<ArticleTypeVO> typeVOS = types.stream().map(TypeConverter.CONVERTER::convert).collect(Collectors.toList());
            pageResult = new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), typeVOS);
        }
        return CommonResultCode.dataResponse(pageResult);
    }

    @Override
    public DataResponse enableArticleTypes() {
        List<Type> allTypes = service.typeTkService().queryList(new Type(true));
        List<ArticleTypeVO> result;
        if (CollectionUtils.isEmpty(allTypes)) {
            result = Collections.emptyList();
        } else {
            result = allTypes.stream().map(TypeConverter.CONVERTER::convert).collect(Collectors.toList());
        }
        return CommonResultCode.dataResponse(result);
    }

    @Override
    public DataResponse addType(TypeDTO typeDTO) {
        String name = typeDTO.getName();
        Type type = service.typeTkService().queryOne(new Type(name));
        if (Objects.nonNull(type)) {
            return BlogResultCode.dataResponse(TYPE_EXIST);
        }
        type = TypeConverter.CONVERTER.convert(typeDTO);
        if (!service.typeTkService().insert(type)) {
            return CommonResultCode.dataResponse(SYSTEM_BUSY);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse editType(TypeDTO typeDTO) {
        Type type = service.typeTkService().queryById(typeDTO.getId());
        if (Objects.isNull(type)) {
            return BlogResultCode.dataResponse(NOT_FOUND_TYPE);
        }
        // check name exist.
        if (!typeDTO.getName().equals(type.getName())) {
            Type byName = service.typeTkService().queryOne(new Type(typeDTO.getName()));
            if (byName != null) {
                return BlogResultCode.dataResponse(TYPE_EXIST);
            }
        }
        //update.
        TypeConverter.CONVERTER.updateByDTO(typeDTO, type);
        if (!service.typeTkService().update(type)) {
            return CommonResultCode.dataResponse(SYSTEM_BUSY);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse deleteType(Integer id) {
        Type type = service.typeTkService().queryById(id);
        if (Objects.isNull(type)) {
            return BlogResultCode.dataResponse(NOT_FOUND_TYPE);
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

        if (Boolean.FALSE.equals(execute)) {
            return CommonResultCode.dataResponse(SYSTEM_BUSY);
        }

        return CommonResultCode.dataResponse();
    }

    private List<Comment> queryByArticles(List<Article> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            return null;
        }
        List<Long> articleIds = articles.stream().map(Article::getId).collect(Collectors.toList());
        return service.commentTkService().queryCommentsByArticleIds(articleIds);
    }
}
