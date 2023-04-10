package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.apps.blog.dto.TypeDTO;
import com.hqy.cloud.apps.blog.vo.ArticleTypeVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;

import java.util.List;

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
     * @return        R
     */
    R<PageResult<ArticleTypeVO>> adminPageTypes(String name, Integer current, Integer size);

    /**
     * 获取能够使用的文章类型
     * @return R.
     */
    R<List<ArticleTypeVO>> enableArticleTypes();

    /**
     * 新增文章类型
     * @param typeDTO {@link TypeDTO}
     * @return        R.
     */
    R<Boolean> addType(TypeDTO typeDTO);

    /**
     * 修改文章类型
     * @param typeDTO {@link TypeDTO}
     * @return        R.
     */
    R<Boolean> editType(TypeDTO typeDTO);

    /**
     * 删除文章类型 -> 包括删除对应的文章、评论
     * @param id 类型id
     * @return   R.
     */
    R<Boolean> deleteType(Integer id);
}
