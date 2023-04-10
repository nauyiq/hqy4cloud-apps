package com.hqy.cloud.apps.blog.converter;

import com.hqy.cloud.apps.blog.dto.ArticleDTO;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.apps.blog.vo.PageArticleVO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/3 9:43
 */
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArticleConverter {

    ArticleConverter CONVERTER = Mappers.getMapper(ArticleConverter.class);

    /**
     * Article convert to PageArticleVO.
     * @param article {@link Article}
     * @return        {@link PageArticleVO}
     */
    @Mapping(source = "created", target = "created", qualifiedByName = "dateConvertString")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusConvertString")
    @Mapping(source = "intro", target = "description")
    PageArticleVO convert(Article article);

    /**
     * update article from articleDTO， not set null property.
     * @param articleDTO {@link ArticleDTO}
     * @param article    {@link Article}
     */
    @Mapping(source = "musicName", target = "backgroundMusicName")
    @Mapping(source = "musicUrl", target = "backgroundMusic")
    @Mapping(source = "description", target = "intro")
    void updateByDTO(ArticleDTO articleDTO, @MappingTarget Article article);

    /**
     * Article convert to ArticleDoc
     * @param article {@link Article}
     * @return        {@link ArticleDoc}
     */
    ArticleDoc convertDoc(Article article);


}
