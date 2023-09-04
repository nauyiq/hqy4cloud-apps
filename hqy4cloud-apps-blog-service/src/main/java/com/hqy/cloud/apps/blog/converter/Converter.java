package com.hqy.cloud.apps.blog.converter;

import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.apps.blog.vo.AccountProfileVO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/4 11:08
 */
@SuppressWarnings("all")
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface Converter {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
    AccountProfileVO convert(AccountInfoDTO accountInfo);




}
