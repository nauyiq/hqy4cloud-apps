package com.hqy.cloud.message.canal.listener;

import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.model.CanalBinLogResult;
import com.hqy.cloud.message.bind.ImMessageConverter;
import com.hqy.cloud.message.cache.ImUnreadCacheService;
import com.hqy.cloud.message.canal.model.CanalImConversation;
import com.hqy.cloud.message.service.ImConversationOperationsService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/25 10:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImConversationCanalListener extends BaseCanalBinlogEventProcessor<CanalImConversation> {
    private final ImUnreadCacheService imUnreadCacheService;
    private final ImConversationTkService imConversationTkService;
    private final ImConversationOperationsService imConversationOperationsService;

    @Override
    protected void processInsertInternal(CanalBinLogResult<CanalImConversation> result) {
        doEvent(result, true);
    }

    @Override
    protected void processUpdateInternal(CanalBinLogResult<CanalImConversation> result) {
        doEvent(result, false);
    }


    private void doEvent(CanalBinLogResult<CanalImConversation> result, boolean insert) {
       try {
           Long primaryKey = result.getPrimaryKey();
           if (primaryKey == null) {
               log.warn("Receive insert event by t_im_conversation, but primaryKey in null.");
               return;
           }
           CanalImConversation data = result.getAfterData();
           if (data == null) {
               ImConversation conversation = imConversationTkService.queryById(primaryKey);
               if (conversation == null) {
                   log.warn("Ignore insert or update event by t_im_conversation, not found entity by {}.", primaryKey);
                   return;
               }
               data = ImMessageConverter.CONVERTER.convert(conversation);
           }

          /* if (ImMessageType.SYSTEM.type.equals(data.getLastMessageType())) {
               // ignore system message
               return;
           }*/

           // ignore self message.
           if (data.getLastMessageFrom().equals(1) || data.getIsRemove().equals(1)) {
               return;
           }
           // increase toContact user unread count.
           if (data.getIsGroup() == 1) {
               imUnreadCacheService.addGroupConversationUnread(data.getUserId(), data.getContactId(), 1L);
           } else {
               imUnreadCacheService.addPrivateConversationUnread(data.getUserId(), primaryKey, 1L);
               //send insert conversation event.
               if (insert) {
                   imConversationOperationsService.sendAppendPrivateChatEvent(ImMessageConverter.CONVERTER.convert(data));
               }
           }
       } catch (Throwable cause) {
           log.error(cause.getMessage(), cause);
       }
    }
}
