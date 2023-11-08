package com.hqy.cloud.message.bind;

import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.common.base.converter.CommonConverter;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.entity.ImMessageHistory;
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

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    UserInfoVO convert(AccountProfileStruct struct);

    @Mapping(target = "imMessageId", source = "id")
    ImMessageHistory convert(ImMessage message);

}
