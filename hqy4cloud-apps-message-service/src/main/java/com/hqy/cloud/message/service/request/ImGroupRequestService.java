package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:38
 */
public interface ImGroupRequestService {

    /**
     * 新建群聊
     * @param creator      创建者
     * @param createGroup {@link GroupDTO}
     * @return            R.
     */
    R<Boolean> createGroup(Long creator, GroupDTO createGroup);

    /**
     * 修改群聊
     * @param info       操作用户
     * @param editGroup {@link GroupDTO}
     * @return          R
     */
    R<Boolean> editGroup(AuthenticationInfo info, GroupDTO editGroup);

    /**
     * 获取群聊成员
     * @param userId  登录用户iD
     * @param groupId 群聊id
     * @return        R.
     */
    R<List<GroupMemberVO>> getGroupMembers(Long userId, Long groupId);

    /**
     * 添加群聊成员
     * @param id          登录用户id
     * @param group       {@link GroupDTO}
     * @return            R.
     */
    R<Boolean> addGroupMember(Long id, GroupDTO group);

    /**
     * 修改群聊成员信息
     * @param id          登录用户id
     * @param groupMember {@link GroupMemberDTO}
     * @return            R.
     */
    R<Boolean> editGroupMember(Long id, GroupMemberDTO groupMember);

    /**
     * 移除群聊成员
     * @param id          登录用户id
     * @param groupMember {@link GroupMemberDTO}
     * @return            R.
     */
    R<Boolean> removeGroupMember(Long id, GroupMemberDTO groupMember);

    /**
     * 退出群聊
     * @param userId  用户id
     * @param groupId 群聊id
     * @return        R.
     */
    R<Boolean> exitGroup(Long userId, Long groupId);

    /**
     * 删除群聊
     * @param userId  用户id
     * @param groupId 群聊id
     * @return        R.
     */
    R<Boolean> deleteGroup(Long userId, Long groupId);
}
