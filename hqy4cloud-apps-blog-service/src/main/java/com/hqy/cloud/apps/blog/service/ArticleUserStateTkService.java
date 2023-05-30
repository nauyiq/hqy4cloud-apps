package com.hqy.cloud.apps.blog.service;

import com.hqy.cloud.apps.blog.entity.ArticleUserState;
import com.hqy.cloud.db.tk.BaseTkService;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 11:39
 */
public interface ArticleUserStateTkService extends BaseTkService<ArticleUserState, Long> {

    /**
     * 批量新增或修改
     * @param states {@link ArticleUserState}
     * @return                result
     */
    boolean insertOrUpdate(List<ArticleUserState> states);

}
