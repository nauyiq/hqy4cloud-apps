package com.hqy.blog.controller;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.service.request.ConfigRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class ConfigController {

    private final ConfigRequestService configRequestService;

    @GetMapping("/aboutMe")
    public DataResponse getAboutMe() {
        return configRequestService.getAboutMe();
    }


}
