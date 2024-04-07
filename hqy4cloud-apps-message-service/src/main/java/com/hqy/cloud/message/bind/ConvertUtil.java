package com.hqy.cloud.message.bind;

import cn.hutool.core.lang.Validator;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.hqy.cloud.message.bind.dto.ImMessageEventContentDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.util.JsonUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/16
 */
@Slf4j
@UtilityClass
public class ConvertUtil {
    public static final String DEFAULT_INDEX = "#";

    public String getIndex(boolean isGroup, String displayName) {
        if (isGroup) {
            return ImLanguageContext.getValue(PropertiesConstants.GROUP_INDEX_KEY);
        }
        if (StringUtils.isBlank(displayName)) {
            return DEFAULT_INDEX;
        }
        char fistChar = displayName.charAt(0);
        String fistChatStr = Character.toString(fistChar);
        return Validator.isLetter(fistChatStr) ? (PinyinUtil.getFirstLetter(fistChar) + "").toUpperCase() : "#";
    }


    public String convertMessageEventContent(Long userId, Long sender, Integer type, String content, boolean isGroup) {
        return convertMessageEventContent(userId, sender, type, content, isGroup, ImLanguageContext.Language.ZH_CH);
    }


    /**
     * 根据消息类型转义消息内容
     * @param userId   用户id
     * @param sender   发送人
     * @param type     消息类型
     * @param content  内容
     * @param language 语言类型
     * @return         转义后的消息内容
     */
    public String convertMessageEventContent(Long userId, Long sender, Integer type, String content, boolean isGroup, ImLanguageContext.Language language) {
        if (!EventMessageType.isEventType(type)) {
            return content;
        }
        EventMessageType messageType = EventMessageType.getType(type);
        if (messageType == null) {
            return content;
        }

        String key = messageType.translateKey;

        // 好友消息单独处理
        if (messageType == EventMessageType.FRIEND) {
            if (sender.equals(userId)) {
                // 发送人等于自己
                content = ImLanguageContext.getValue(key, language);
            }
            return content;
        }

        if (messageType == EventMessageType.ADD_FRIEND_NOTICE) {
            return ImLanguageContext.getValue(key, language);
        }


        if (StringUtils.isNotBlank(key)) {
            // 需要翻译内容
            try {
                ImMessageEventContentDTO eventContentDTO = JsonUtil.toBean(content, ImMessageEventContentDTO.class);
                Long operatorId = eventContentDTO.getOperatorId();
                String operatorName = eventContentDTO.getOperatorName();
                String value = ImLanguageContext.getValue(key, language);
                if (sender.equals(operatorId)) {
                    content = getReplaceValue(value, ImLanguageContext.getYouValue(language));
                } else {
                    content = isGroup ? getReplaceValue(value, operatorName) : getReplaceValue(value, ImLanguageContext.getTargetValue(language));
                }
            } catch (Throwable cause) {
                log.error(cause.getMessage(), cause);
            }
        }
        return content;
    }

    public String getReplaceValue(String source, String value) {
        if (source.contains(Constants.REPLACE)) {
            return source.replace(Constants.REPLACE, value);
        }
        return value + source;
    }









}
