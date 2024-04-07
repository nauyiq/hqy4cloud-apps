package com.hqy.cloud.apps.blog.server;

import com.hqy.cloud.shardingsphere.algorithm.ConsistentHashPreciseShardingAlgorithm;

/**
 * 根据id进行一致性hash算法
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/31
 */
public class CommentIdConsistentHashShardingAlgorithm extends ConsistentHashPreciseShardingAlgorithm<Long> {
}
