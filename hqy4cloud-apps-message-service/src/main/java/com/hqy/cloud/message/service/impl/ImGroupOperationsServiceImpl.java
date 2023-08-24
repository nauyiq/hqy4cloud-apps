package com.hqy.cloud.message.service.impl;

import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.bind.event.support.AddGroupEvent;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImGroup;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupOperationsServiceImpl implements ImGroupOperationsService {
    private final TransactionTemplate template;
    private final ImConversationTkService contactTkService;
    private final ImGroupTkService groupTkService;
    private final ImGroupMemberTkService groupMemberTkService;
    private final ImEventListener eventListener;



    @Override
    public boolean createGroup(Long id, GroupDTO createGroup) {
        ImGroup group = ImGroup.of(createGroup.getName(), id, new Date());
        List<ImGroupMember> members = template.execute(status -> {
            try {
                // insert group.
                AssertUtil.isTrue(groupTkService.insert(group), "Failed execute to insert group.");
                Long groupId = group.getId();
                // insert group members
                List<ImGroupMember> groupMembers = ImGroupMember.of(groupId, id, createGroup.getUserIds());
                AssertUtil.isTrue(groupMemberTkService.insertList(groupMembers), "Failed execute to insert group members.");
                // insert conversation.
                List<ImConversation> conversations = ImConversation.ofGroup(groupId, id, createGroup.getUserIds());
                AssertUtil.isTrue(contactTkService.insertList(conversations), "Failed execute to insert conversations by create group.");
                return groupMembers;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return null;
            }
        });

        if (CollectionUtils.isNotEmpty(members)) {
            List<AddGroupEvent> addGroupEvents = ConvertUtil.newAddGroupEvent(members, group);
            eventListener.onAddGroup(addGroupEvents);
            return true;
        }
        return false;
    }

    @Override
    public boolean editGroup(GroupMemberDTO info, GroupDTO editGroup) {
        boolean editGroupName = true;
        boolean editNotice = true;
        String name = editGroup.getName();
        String notice = editGroup.getNotice();
        if (StringUtils.isBlank(name) || name.equals(info.getGroupName())) {
            editGroupName = false;
        }
        if (StringUtils.isBlank(notice) || notice.equals(info.getNotice())) {
            editNotice = false;
        }
        if (!editGroupName && !editNotice) {
            return true;
        }

        ImGroup group = ImGroup.of(info.getGroupId());
        if (editGroupName) {
            group.setName(name);
        }
        if (editNotice) {
            group.setNotice(notice);
        }

        if (groupTkService.updateSelective(group)) {
            if (editGroupName) {
                //TODO send edit group name event.
            }
            if (editNotice) {
                //TODO send edit group notice event.
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addGroupMember(GroupMemberDTO groupMember) {
        ImGroupMember imGroupMember = ImGroupMember.of(groupMember.getGroupId(), groupMember.getId(), groupMember.getGroupName(), GroupRole.COMMON.role);
        ImConversation contact = ImConversation.ofGroup(groupMember.getId(), groupMember.getGroupId());
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupMemberTkService.insert(imGroupMember), "Failed execute to insert group members.");
                AssertUtil.isTrue(contactTkService.insert(contact), "Failed execute to insert group contact.");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            //TODO send socket messages
            return true;
        }
        return false;
    }

    @Override
    public boolean removeGroupMember(Long id, Long groupId) {
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupMemberTkService.delete(new ImGroupMember(groupId, id)), "Failed execute to delete group member.");
                AssertUtil.isTrue(contactTkService.delete(ImConversation.of(id, groupId, true)), "Failed execute to delete group contact.");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            //TODO send socket messages
            return true;
        }

        return false;
    }

    @Override
    public boolean isGroupMember(Long id, Long groupId) {
        ImGroupMember member = groupMemberTkService.queryOne(new ImGroupMember(groupId, id));
        return member != null;
    }


}
