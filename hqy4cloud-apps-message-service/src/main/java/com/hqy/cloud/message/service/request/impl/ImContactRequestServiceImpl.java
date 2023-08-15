package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.service.request.ImContactRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImContactRequestServiceImpl implements ImContactRequestService {



    @Override
    public R<ContactVO> getContacts(Long id) {
        return null;
    }
}
