package com.hqy.cloud.message.bind;

import com.hqy.cloud.common.base.converter.CommonConverter;
import com.hqy.cloud.message.canal.model.CanalImConversation;
import com.hqy.cloud.message.tk.entity.ImConversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/24 14:21
 */
@SuppressWarnings("all")
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImMessageConverter {

    ImMessageConverter CONVERTER = Mappers.getMapper(ImMessageConverter.class);

    @Mapping(target = "isNotice", source = "notice", qualifiedByName = "booleanToInteger")
    @Mapping(target = "isGroup", source = "group", qualifiedByName = "booleanToInteger")
    @Mapping(target = "isTop", source = "top", qualifiedByName = "booleanToInteger")
    CanalImConversation convert(ImConversation conversation);





}
