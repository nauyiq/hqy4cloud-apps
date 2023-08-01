package com.hqy.cloud.apps.blog.service;

import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/1 17:20
 */
@ThriftService(MicroServiceConstants.BLOG_SERVICE)
public interface SocketIoBlogPushService extends ThriftSocketIoPushService {
}
