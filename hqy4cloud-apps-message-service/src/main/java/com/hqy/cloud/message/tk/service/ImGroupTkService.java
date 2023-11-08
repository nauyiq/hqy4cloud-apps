package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.GroupContactDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroup;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:28
 */
public interface ImGroupTkService extends BaseTkService<ImGroup, Long> {

    /**
     * abstain user info in group.
     * @param id      user id
     * @param groupId group id
     * @return        group info.
     */
    GroupMemberDTO getGroupMemberInfo(Long id, Long groupId);

    /**
     * 获取用户创建的群聊
     * @param userId 用户id
     * @return       {@link GroupContactDTO}
     */
    List<GroupContactDTO> queryGroupContact(Long userId);
}
