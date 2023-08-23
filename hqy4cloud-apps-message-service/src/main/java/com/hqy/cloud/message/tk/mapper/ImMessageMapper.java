package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.tk.entity.ImMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:36
 */
@Repository
public interface ImMessageMapper extends BaseTkMapper<ImMessage, Long> {

    /**
     * 获取消息未读
     * @param to  发给谁
     * @param ids 谁发送的
     * @return    {@link MessageUnreadDTO}
     */
    List<MessageUnreadDTO> getMessageUnread(@Param("to") Long to, @Param("ids") List<Long> ids);

}
