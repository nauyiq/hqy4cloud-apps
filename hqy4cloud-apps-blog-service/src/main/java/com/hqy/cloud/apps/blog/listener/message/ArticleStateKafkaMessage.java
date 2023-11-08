package com.hqy.cloud.apps.blog.listener.message;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.bind.MessageParams;
import lombok.*;

import java.util.Objects;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Blog.BLOG_STATE_TOPIC;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 10:13
 */
@Data
@Getter
@NoArgsConstructor
public class ArticleStateKafkaMessage implements MessageModel {

    private Long articleId;
    private Long accountId;
    private int status;
    private long timestamp = System.currentTimeMillis();

    public static ArticleStateKafkaMessage of(Long articleId, Long accountId, int status) {
        return new ArticleStateKafkaMessage(articleId, accountId, status);
    }

    public ArticleStateKafkaMessage(Long articleId, Long accountId, int status) {
        this.articleId = articleId;
        this.accountId = accountId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleStateKafkaMessage that = (ArticleStateKafkaMessage) o;
        return articleId.equals(that.articleId) && accountId.equals(that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, accountId);
    }

    @Override
    public MessageParams getParameters() {
        return MessageParams.of(BLOG_STATE_TOPIC, StrUtil.EMPTY);
    }

    @Override
    public String jsonPayload() {
        return JsonUtil.toJson(this);
    }
}
