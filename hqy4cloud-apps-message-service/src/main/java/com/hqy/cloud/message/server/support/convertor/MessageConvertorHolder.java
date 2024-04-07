package com.hqy.cloud.message.server.support.convertor;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.foundation.spi.SpiInstanceServiceLoad;
import com.hqy.cloud.message.server.MessageConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * SPI扫描消息转换器, 并且注入到上下文中
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
@Slf4j
public class MessageConvertorHolder {
    private static final Map<Integer, MessageConvertor> CONVERTOR_MAP = MapUtil.newHashMap(8);

    static {
        SpiInstanceServiceLoad.register(MessageConvertor.class);
        Collection<MessageConvertor> serviceInstances = SpiInstanceServiceLoad.getServiceInstances(MessageConvertor.class);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            log.warn("Not found message converter by spi.");
        } else {
            for (MessageConvertor convertor : serviceInstances) {
                List<Integer> types = convertor.supportTypes();
                types.forEach(type -> CONVERTOR_MAP.put(type, convertor));
            }
        }
    }

    public static MessageConvertor getConvertor(Integer type) {
        MessageConvertor convertor = CONVERTOR_MAP.get(type);
        if (convertor == null) {
            throw new UnsupportedOperationException("Not support type " + type);
        }
        return convertor;
    }

}
