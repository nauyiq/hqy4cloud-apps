package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.entity.ArticleUserState;
import com.hqy.cloud.tk.BaseTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 11:37
 */
@Repository
public interface ArticleUserStateMapper extends BaseTkMapper<ArticleUserState, Long> {

    /**
     * 批量新增或修改
     * @param states {@link ArticleUserState}
     * @return       影响的行数
     */
    int insertOrUpdate(@Param("states")List<ArticleUserState> states);
}
