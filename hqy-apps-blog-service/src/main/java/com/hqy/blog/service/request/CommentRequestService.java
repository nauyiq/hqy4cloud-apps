package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;

/**
 * CommentRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:44
 */
public interface CommentRequestService {

    /**
     * 获取分页评论列表
     * @param pageNumber 第几页？
     * @param pageSize   一页几行？
     * @return           DataResponse.
     */
    DataResponse getPageComments(Integer pageNumber, Integer pageSize);
}
