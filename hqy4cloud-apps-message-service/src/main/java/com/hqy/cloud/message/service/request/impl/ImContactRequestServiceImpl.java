package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.request.ImContactRequestService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImContactRequestServiceImpl implements ImContactRequestService {
    private final ImConversationTkService contactTkService;
    private final ImGroupTkService groupTkService;
    private final ImFriendTkService imFriendTkService;
    private final ImFriendOperationsService friendOperationsService;

    @Override
    public R<List<ConversationVO>> getContacts(Long id) {
        //获取联系人列表
        Example example = new Example(ImConversation.class);
        example.createCriteria().andEqualTo("userId", id);
        example.orderBy("top").desc().orderBy("lastMessageTime").desc();
        List<ImConversation> contacts = contactTkService.queryByExample(example);
        List<ConversationVO> vos = CollectionUtils.isEmpty(contacts) ? Collections.emptyList()
                : convert(contacts);
        return R.ok(vos);
    }

    private List<ConversationVO> convert(final List<ImConversation> contacts) {
        /*Map<Boolean, List<ImContact>> map = contacts.parallelStream().collect(Collectors.groupingBy(ImContact::getGroup));
        List<ImContact> groupContacts = map.get(true);
        Map<Long, String> groupNameMap = CollectionUtils.isEmpty(groupContacts) ? MapUtil.newHashMap()
                : groupTkService.getGroupNames(groupContacts.parallelStream().map(ImContact::getContactId).collect(Collectors.toList()));

        List<ImContact> friendContacts = map.get(false);
        List<Long> ids = friendContacts.parallelStream().map(ImContact::getContactId).collect(Collectors.toList());
        Map<Long, String> friendNameMap = CollectionUtils.isEmpty(friendContacts) ? MapUtil.newHashMap()
                : imFriendTkService.getFriedsMarks(ids);
        Map<Long, AccountBaseInfoStruct> accountBaseInfoMap = AccountRpcUtil.getAccountBaseInfoMap(ids);

        return contacts.stream().map(contact -> {
            ContactVO.builder()
                    .

        });*/
        return null;
    }


}
