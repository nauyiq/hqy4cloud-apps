package com.hqy.blog.controller;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.service.request.CommentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:41
 */
@Service
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentRequestService commentRequestService;

    @GetMapping("/admin/comments")
    public DataResponse comments(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        return commentRequestService.getPageComments(pageNumber, pageSize);
    }


}
