package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;

/**
 * ArticleTypeRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:53
 */
public interface ArticleTypeRequestService {

    /**
     * 获取所有文章的类型.
     * @return DataResponse.
     */
    DataResponse articleTypes();

    /**
     * 获取能够使用的文章类型
     * @return DataResponse.
     */
    DataResponse enableArticleTypes();


}
