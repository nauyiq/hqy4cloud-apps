package com.hqy.blog.entity;

import com.google.common.base.Objects;
import com.hqy.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Table;

/**
 * entity for t_config.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:40
 */
@Data
@Table(name = "t_config")
@AllArgsConstructor
@NoArgsConstructor
public class Config extends BaseEntity<Integer> {

    /**
     * 关于我
     */
    private String aboutMe;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("aboutMe", aboutMe)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Config config = (Config) o;
        return Objects.equal(aboutMe, config.aboutMe);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), aboutMe);
    }
}
