package com.hqy.cloud.apps.blog.service.tk.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.apps.blog.entity.Type;
import com.hqy.cloud.apps.blog.mapper.TypeMapper;
import com.hqy.cloud.apps.blog.service.tk.TypeTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:34
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TypeTkServiceImpl extends BaseTkServiceImpl<Type, Integer> implements TypeTkService {

    private final TypeMapper typeMapper;

    @Override
    public BaseTkMapper<Type, Integer> getTkMapper() {
        return typeMapper;
    }

    @Override
    public PageInfo<Type> queryPageTypes(String name, Integer current, Integer size) {
        Example example = new Example(Type.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", false);
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", PERCENT + name + PERCENT);
        }
        PageHelper.startPage(current, size);
        List<Type> types = typeMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(types)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(types);
    }
}
