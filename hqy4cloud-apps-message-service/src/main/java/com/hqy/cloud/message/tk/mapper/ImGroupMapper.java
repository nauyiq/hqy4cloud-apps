package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.message.bind.dto.GroupContactDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:27
 */
@Repository
public interface ImGroupMapper extends BaseTkMapper<ImGroup, Long> {

    /**
     * abstain user info in group.
     * @param id      user id
     * @param groupId group id
     * @return        group info.
     */
    GroupMemberDTO getGroupMemberInfo(@Param("id") Long id, @Param("groupId")Long groupId);

    /**
     * 获取用户创建的群聊
     * @param userId 用户id
     * @return       {@link GroupContactDTO}
     */
    List<GroupContactDTO> queryGroupContact(@Param("creator") Long userId);
}
