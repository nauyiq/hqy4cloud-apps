package com.hqy.cloud.message.server.support.convertor;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.message.bind.dto.FileMessageDTO;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.server.AbstractMessageConvertor;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 文件消息转换器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
@Slf4j
public class FileMessageConvertor extends AbstractMessageConvertor {

    @Override
    public List<Integer> supportTypes() {
        return List.of(MessageType.FILE.type, MessageType.IMAGE.type);
    }

    @Override
    protected ImMessageVO doProcess(Long loginUser, ImMessageVO message) {
        // 消息内容.
        String content = message.getContent();
        if (StringUtils.isBlank(content)) {
            log.warn("Message content is empty, isGroup:{},  messageId:{}.", message.getIsGroup(), message.getMessageId());
            return message;
        }
        // 转成文件消息对象
        try {
            FileMessageDTO fileMessage = JsonUtil.toBean(content, FileMessageDTO.class);
            message.setFileName(fileMessage.getFileName());
            message.setFileSize(fileMessage.getFileSize());
            message.setContent(fileMessage.getPath());
            return message;
        } catch (Throwable cause) {
            log.warn(cause.getMessage());
            return message;
        }
    }


    @Override
    public String processByConversation(Long loginUser, String content, boolean group) {
        // 文件消息类型无需展示最后一条消息内容
        return StrUtil.EMPTY;
    }
}
