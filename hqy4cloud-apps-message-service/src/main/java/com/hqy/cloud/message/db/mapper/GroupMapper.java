package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.db.entity.Group;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * im群聊表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
public interface GroupMapper extends BasePlusMapper<Group> {

    /**
     * 伪删除群聊
     * @param groupId 群聊id
     * @return        是否删除成功
     */
    int deleteGroup(@Param("groupId") Long groupId);
}
