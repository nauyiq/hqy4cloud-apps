package com.hqy.cloud.message.service.request;

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
     * @param id          创建者id
     * @param createGroup {@link GroupDTO}
     * @return            R.
     */
    R<Boolean> createGroup(Long id, GroupDTO createGroup);

    /**
     * 修改群聊
     * @param id        操作用户id
     * @param editGroup {@link GroupDTO}
     * @return          R
     */
    R<Boolean> editGroup(Long id, GroupDTO editGroup);

    /**
     * 获取群聊成员
     * @param groupId 群聊id
     * @return        R.
     */
    R<List<GroupMemberVO>> getGroupMembers(Long groupId);

    /**
     * 添加群聊成员
     * @param id          登录用户id
     * @param groupMember {@link GroupMemberDTO}
     * @return            R.
     */
    R<Boolean> addGroupMember(Long id, GroupMemberDTO groupMember);

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
}
