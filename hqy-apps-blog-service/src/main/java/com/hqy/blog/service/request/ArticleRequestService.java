package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.dto.ArticleDTO;

/**
 * ArticleRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:46
 */
public interface ArticleRequestService {

    /**
     * 新增一遍文章.
     * @param articleDTO article data.
     * @return           DataResponse.
     */
    DataResponse ArticleRequestService(ArticleDTO articleDTO);
}
