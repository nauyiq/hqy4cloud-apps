package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:23
 */
@Repository
public interface ImFriendApplicationMapper extends PrimaryLessTkMapper<ImFriendApplication> {

    /**
     * 新增或更新
     * @param application entity
     * @return            rows
     */
    int insertDuplicate(@Param("application") ImFriendApplication application);
}
