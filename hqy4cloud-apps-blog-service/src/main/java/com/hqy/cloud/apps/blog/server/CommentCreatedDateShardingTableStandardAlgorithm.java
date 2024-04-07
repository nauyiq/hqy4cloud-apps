package com.hqy.cloud.apps.blog.server;

import com.hqy.cloud.shardingsphere.algorithm.DateShardingTableStandardAlgorithm;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
public class CommentCreatedDateShardingTableStandardAlgorithm extends DateShardingTableStandardAlgorithm {

    @Override
    protected String getLogicTableName() {
        return "t_comment";
    }
}
