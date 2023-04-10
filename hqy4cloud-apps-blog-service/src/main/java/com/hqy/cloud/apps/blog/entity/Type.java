package com.hqy.cloud.apps.blog.entity;

import com.google.common.base.Objects;
import com.hqy.cloud.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Table;

/**
 * entity for t_type.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:32
 */
@Data
@Table(name = "t_type")
@AllArgsConstructor
@NoArgsConstructor
public class Type extends BaseEntity<Integer> {

    /**
     * 类型名
     */
    private String name;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否删除
     */
    private Boolean deleted = false;

    public Type(String name) {
        this.name = name;
    }

    public Type(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("status", status)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Type type = (Type) o;
        return Objects.equal(name, type.name) && Objects.equal(status, type.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), name, status);
    }
}
