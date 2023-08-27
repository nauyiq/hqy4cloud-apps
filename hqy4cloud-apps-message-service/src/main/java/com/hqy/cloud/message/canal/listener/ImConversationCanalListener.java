package com.hqy.cloud.message.canal.listener;

import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.model.CanalBinLogResult;
import com.hqy.cloud.message.bind.ImMessageConverter;
import com.hqy.cloud.message.canal.model.CanalImConversation;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.service.ImMessageOperationsService;
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
    private final ImConversationTkService imConversationTkService;
    private final ImMessageOperationsService imMessageOperationsService;

    @Override
    protected void processInsertInternal(CanalBinLogResult<CanalImConversation> result) {
        doEvent(result);
    }

    @Override
    protected void processUpdateInternal(CanalBinLogResult<CanalImConversation> result) {
        doEvent(result);
    }

    private void doEvent(CanalBinLogResult<CanalImConversation> result) {
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
           if (ImMessageType.SYSTEM.type.equals(data.getLastMessageType())) {
               // ignore system message
               return;
           }
           if (data.getLastMessageFrom().equals(1)) {
               // ignore self message.
               return;
           }

           // increase toContact user unread count.
           imMessageOperationsService.increaseConversationUnread(data.getUserId(), primaryKey);
       } catch (Throwable cause) {
           log.error(cause.getMessage(), cause);
       }
    }

    @Override
    protected void processDeleteInternal(CanalBinLogResult<CanalImConversation> result) {
        try {
            Long primaryKey = result.getPrimaryKey();
            if (primaryKey == null) {
                log.warn("Receive delete event by t_im_conversation, but primaryKey in null.");
                return;
            }
            CanalImConversation data = result.getBeforeData();
            if (data == null || ImMessageType.SYSTEM.type.equals(data.getLastMessageType())) {
                return;
            }
            imMessageOperationsService.removeConversationUnread(data.getContactId(), primaryKey);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }
    }
}
