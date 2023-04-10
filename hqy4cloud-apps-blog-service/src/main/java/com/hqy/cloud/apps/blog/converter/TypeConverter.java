package com.hqy.cloud.apps.blog.converter;

import com.hqy.cloud.apps.blog.dto.TypeDTO;
import com.hqy.cloud.apps.blog.entity.Type;
import com.hqy.cloud.apps.blog.vo.ArticleTypeVO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 15:32
 */
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TypeConverter {

    TypeConverter CONVERTER = Mappers.getMapper(TypeConverter.class);

    /**
     * Type convert to ArticleTypeVO
     * @param type {@link Type}
     * @return     {@link ArticleTypeVO}
     */
    @Mapping(source = "created", target = "created", qualifiedByName = "dateConvertString")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusConvertString")
    ArticleTypeVO convert(Type type);

    /**
     * TypeDTO convert to Type
     * @param typeDTO {@link TypeDTO}
     * @return        {@link Type}
     */
    Type convert(TypeDTO typeDTO);

    /**
     * update type from typeDto， not set null property.
     * @param typeDTO {@link TypeDTO}
     * @param type    {@link Type}
     */
    void updateByDTO(TypeDTO typeDTO, @MappingTarget Type type);



}
