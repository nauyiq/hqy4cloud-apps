package com.hqy.blog.converter;

import com.hqy.base.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 14:47
 */
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentConverter {

    CommentConverter CONVERTER = Mappers.getMapper(CommentConverter.class);

}
