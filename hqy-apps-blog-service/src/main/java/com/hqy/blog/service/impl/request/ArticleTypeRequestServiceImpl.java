package com.hqy.blog.service.impl.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.entity.Type;
import com.hqy.blog.service.ArticleTypeTagsCompositeService;
import com.hqy.blog.service.TypeTkService;
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

    private final ArticleTypeTagsCompositeService service;

    @Override
    public DataResponse articleTypes() {
        TypeTkService typeTkService = service.typeTkService();
        List<Type> types = typeTkService.queryList(new Type(true));
        List<ArticleTypeVO> articleTypes;
        if (CollectionUtils.isEmpty(types)) {
            articleTypes = Collections.emptyList();
        } else {
            articleTypes = types.stream().map(type -> new ArticleTypeVO(type.getId(), type.getName())).collect(Collectors.toList());
        }
        return CommonResultCode.dataResponse(articleTypes);
    }

}
