package com.hqy.cloud.message.es.service;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.service.ElasticService;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.es.document.ImMessage;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@NoRepositoryBean
public interface ImMessageElasticService extends ElasticService<String, ImMessage> {

    /**
     * 分页查找聊天记录，根据关键词类型等查询
     * @param page           第几页
     * @param limit          一页几条
     * @param accountId      账号id
     * @param contactId      联系人id
     * @param lastRemoveTime 上次移除会话时间
     * @param messageType    消息类型
     * @param keywords       关键词
     * @param group          是否群聊
     * @return               聊天记录
     */
    PageResult<ImMessage> searchPageMessages(Integer page, Integer limit, Long accountId, Long contactId, Long lastRemoveTime, MessageType messageType, String keywords, Boolean group);

    /**
     * 分页查找聊天记录，根据关键词类型等查询， 查询被移除群聊用户可搜索的聊天记录
     * @param page              第几页
     * @param limit             一页几条
     * @param groupConversation 上次移除会话时间
     * @param messageType       消息类型
     * @param keywords          关键词
     * @return                  聊天记录
     */
    PageResult<ImMessage> searchRemovedGroupMemberMessages(Integer page, Integer limit, GroupConversation groupConversation, MessageType messageType, String keywords);
}
