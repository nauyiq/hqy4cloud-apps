package com.hqy.blog.service;

/**
 * BlogDbOperationService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:50
 */
public interface BlogDbOperationService {

    LikedTkService likedTkService();

    TagsTkService tagsTkService();

    TypeTkService typeTkService();

    CommentTkService commentTkService();

    ArticleTkService articleTkService();

    ConfigTkService configTkService();

}
