package com.hqy.cloud.message.db.canal;

import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.model.CanalBinLogResult;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.es.document.ImMessage;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 私聊表 canal处理器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImPrivateMessageCanalProcessor extends BaseCanalBinlogEventProcessor<CanalPrivateMessageModel> {
    private final ImMessageElasticService messageEsService;

    @Override
    protected void processInsertInternal(CanalBinLogResult<CanalPrivateMessageModel> result) {
        afterSaveOrUpdateEsMessage(result, false);
    }

    @Override
    protected void processUpdateInternal(CanalBinLogResult<CanalPrivateMessageModel> result) {
        afterSaveOrUpdateEsMessage(result, true);
    }

    @Override
    protected void processDeleteInternal(CanalBinLogResult<CanalPrivateMessageModel> result) {
        CanalPrivateMessageModel model = result.getAfterData();
        if (model != null) {
            ImMessage imMessage = model.convertToMessageDoc();
            messageEsService.deleteById(imMessage.getId());
        }
    }

    private void afterSaveOrUpdateEsMessage(CanalBinLogResult<CanalPrivateMessageModel> result, boolean update) {
        CanalPrivateMessageModel model = result.getAfterData();
        if (model != null) {
            Integer modelType = model.getType();
            ImMessage imMessage = model.convertToMessageDoc();
            if (!MessageType.enabledForwardOrSearchEs(modelType)) {
                if (update) {
                    // 如果是更新的话 说明这个消息类型不用存储到es，从es中移除
                    messageEsService.deleteById(imMessage.getId());
                }
                return;
            }
            messageEsService.save(imMessage);
        }
    }
}
