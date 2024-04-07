package com.hqy.cloud.message.service.transactional;

import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import io.seata.rm.tcc.api.BusinessActionContext;

/**
 * TCC 添加聊天用户service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/20
 */
public interface TccAddImUserService {


    /**
     * 添加聊天用户
     * @param userInfo 用户信息
     * @return         是否添加成功
     */
    boolean addImUser(ImUserInfoDTO userInfo);

    /**
     * 提交阶段，
     * @param actionContext 上下文
     * @return              是否成功
     */
    boolean commit(BusinessActionContext actionContext);

    /**
     * 回滚阶段
     * @param actionContext 上下文
     * @return              是否成功
     */
    boolean rollback(BusinessActionContext actionContext);

}
