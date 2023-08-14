package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.service.request.ImGroupRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 群聊相关API
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:33
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
public class ImGroupController extends BaseController {
    private final ImGroupRequestService requestService;

    /**
     * 新建群聊
     * @param request     HttpServletRequest
     * @param createGroup {@link GroupDTO}
     * @return            R.
     */
    @PostMapping("/group")
    public R<Boolean> createGroup(HttpServletRequest request, @RequestBody GroupDTO createGroup) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (StringUtils.isBlank(createGroup.getName())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.createGroup(id, createGroup);
    }





}
