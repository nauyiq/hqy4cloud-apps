package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.ImUserSettingInfoDTO;
import com.hqy.cloud.message.bind.vo.UserCardVO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
public interface ImUserSettingRequestService {

    /**
     * 返回当前用户聊天设置信息
     * @param accountId 用户id
     * @return          R -> 用户聊天设置信息
     */
    R<UserImSettingVO> getUserImSettingInfo(Long accountId);

    /**
     * 更新用户聊天设置信息
     * @param accountId 登录的用户id
     * @param setting   用户聊天设置
     * @return          R -> 是否成功
     */
    R<Boolean> updateUserImSettingInfo(Long accountId, UserImSettingVO setting);

    /**
     * 修改用户聊天信息
     * @param accountId 用户id
     * @param userInfo  用户聊天信息
     * @return          是否成功
     */
    R<Boolean> updateImUserInfo(Long accountId, ImUserSettingInfoDTO userInfo);

    /**
     * 根据id查询用户聊天信息
     * @param id     登录id
     * @param userId 用户id
     * @return       R -> 用户聊天信息
     */
    R<UserCardVO> getImUserInfo(Long id, Long userId);

    /**
     * 查询聊天项目中用户列表
     * @param accountId 当前登录用户id
     * @param name      名字
     * @return          R -> 用户列表
     */
    R<List<UserInfoVO>> searchImUsers(Long accountId, String name);


}
