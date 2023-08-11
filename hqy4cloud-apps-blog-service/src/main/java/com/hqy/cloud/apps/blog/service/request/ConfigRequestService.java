package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.common.bind.R;

/**
 * ConfigRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:31
 */
public interface ConfigRequestService {

    /**
     * 获取关于我.
     * @return R.
     */
    R<String> getAboutMe();

}
