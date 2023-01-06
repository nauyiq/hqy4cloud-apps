package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.dto.TypeDTO;

/**
 * ArticleTypeRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:53
 */
public interface ArticleTypeRequestService {

    /**
     * 分页获取文章类型
     * @param name    类型名-模糊查询
     * @param current 当前页
     * @param size    页行数
     * @return        DataResponse
     */
    DataResponse adminPageTypes(String name, Integer current, Integer size);

    /**
     * 获取能够使用的文章类型
     * @return DataResponse.
     */
    DataResponse enableArticleTypes();

    /**
     * 新增文章类型
     * @param typeDTO {@link TypeDTO}
     * @return        DataResponse.
     */
    DataResponse addType(TypeDTO typeDTO);

    /**
     * 修改文章类型
     * @param typeDTO {@link TypeDTO}
     * @return        DataResponse.
     */
    DataResponse editType(TypeDTO typeDTO);

    /**
     * 删除文章类型 -> 包括删除对应的文章、评论
     * @param id 类型id
     * @return   DataResponse.
     */
    DataResponse deleteType(Integer id);
}
