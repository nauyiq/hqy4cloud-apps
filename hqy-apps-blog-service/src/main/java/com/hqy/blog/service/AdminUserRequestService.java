package com.hqy.blog.service;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.dto.BlogUserProfileDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:25
 */
public interface AdminUserRequestService {

    /**
     * get login user info.
     * @param id user id.
     * @return   DataResponse.
     */
    DataResponse getLoginUserInfo(Long id);

    /**
     * update login user profile info.
     * @param profile profile data.
     * @return        DataResponse.
     */
    DataResponse updateLoginUserInfo(BlogUserProfileDTO profile);
}
