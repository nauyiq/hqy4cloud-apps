package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.message.tk.entity.ImMessage;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:36
 */
@Repository
public interface ImMessageMapper extends BaseTkMapper<ImMessage, Long> {
}
