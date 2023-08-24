package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.foundation.common.bind.SocketIoConnection;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 13:19
 */
public interface ImMessageRequestService {


    /**
     * 获取socket.io连接
     * @param request req
     * @param bizId   socketio connection id
     * @return {@link SocketIoConnection}
     */
    R<SocketIoConnection> genWsMessageConnection(HttpServletRequest request, String bizId);

    /**
     * 获取聊天记录
     * @param id     用户id
     * @param params 请求参数
     * @return       R.
     */
    R<PageResult<ImMessageVO>> getImMessages(Long id, MessagesRequestParamDTO params);

    /**
     * send message to user or group
     * @param id      current user id.
     * @param message {@link ImMessageDTO}
     * @return        R.
     */
    R<ImMessageVO> sendImMessage(Long id, ImMessageDTO message);
}
