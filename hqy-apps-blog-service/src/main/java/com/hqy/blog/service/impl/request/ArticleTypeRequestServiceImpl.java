package com.hqy.blog.service.impl.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.entity.Type;
import com.hqy.blog.service.BlogDbOperationService;
import com.hqy.blog.service.request.ArticleTypeRequestService;
import com.hqy.blog.vo.ArticleTypeVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:54
 */
@Service
@RequiredArgsConstructor
public class ArticleTypeRequestServiceImpl implements ArticleTypeRequestService {

    private final BlogDbOperationService service;

    @Override
    public DataResponse articleTypes() {
        return CommonResultCode.dataResponse(getArticleTypes(true));
    }

    @Override
    public DataResponse enableArticleTypes() {
        return CommonResultCode.dataResponse(getArticleTypes(false));
    }

    private List<ArticleTypeVO> getArticleTypes(boolean queryAll) {
        List<Type> allTypes;
        List<ArticleTypeVO> result;
        if (queryAll) {
            allTypes = service.typeTkService().queryAll();
        } else {
            allTypes = service.typeTkService().queryList(new Type(true));
        }

        if (CollectionUtils.isEmpty(allTypes)) {
            result = Collections.emptyList();
        } else {
            result = allTypes.stream().map(type -> new ArticleTypeVO(type.getId(), type.getName())).collect(Collectors.toList());
        }
        return result;
    }


}
