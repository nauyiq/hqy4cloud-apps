package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.service.request.ImContactRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 聊天联系人相关API
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:39
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
public class ImContactController extends BaseController {
    private final ImContactRequestService requestService;

    /**
     * 获取当前用户的聊天联系人
     * @param request HttpServletRequest.
     * @return        R.
     */
    @PostMapping("/contacts")
    public R<ContactVO> getContacts(HttpServletRequest request) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getContacts(id);
    }


}
