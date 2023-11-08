package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.entity.ChatgptMessageHistory;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:25
 */
@Repository
public interface ChatgptMessageHistoryMapper extends BaseTkMapper<ChatgptMessageHistory, Long> {
}
