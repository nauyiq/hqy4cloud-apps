package com.hqy.blog.service.impl.request;

import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.ArticleDTO;
import com.hqy.blog.entity.Article;
import com.hqy.blog.service.ArticleTypeTagsCompositeService;
import com.hqy.blog.service.request.ArticleRequestService;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:47
 */
@Service
@RequiredArgsConstructor
public class ArticleRequestServiceImpl implements ArticleRequestService {

    private final ArticleTypeTagsCompositeService articleTypeTagsCompositeService;

    @Override
    public DataResponse ArticleRequestService(ArticleDTO articleDTO) {
        boolean exist = checkTypeExist(articleDTO.getType());
        if (!exist) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_TYPE);
        }

        //insert article to db.
        long id = ProjectSnowflakeIdWorker.getInstance().nextId();
        Date date = new Date();
        Article article = new Article(id, articleDTO.getTitle(), articleDTO.getDescription(), articleDTO.getCover(), articleDTO.getContent(), articleDTO.getType(),
                articleDTO.getMusicUrl(), articleDTO.getMusicName(), articleDTO.getAuthor(), true, date);
        if (!articleTypeTagsCompositeService.articleTkService().insert(article)) {
            return CommonResultCode.dataResponse(CommonResultCode.SYSTEM_ERROR_INSERT_FAIL);
        }

        return CommonResultCode.dataResponse();
    }

    private boolean checkTypeExist(Integer type) {
        if (type == null) {
            return false;
        }
        return articleTypeTagsCompositeService.typeTkService().queryById(type) != null;
    }


}
