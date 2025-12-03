package com.lq.yingge_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lq.yingge_backend.mapper.ClothTemplateMapper;
import com.lq.yingge_backend.model.entity.ClothTemplate;
import com.lq.yingge_backend.service.ClothTemplateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ClothTemplateServiceImpl implements ClothTemplateService {

    private final ClothTemplateMapper clothTemplateMapper;

    @Override
    public IPage<ClothTemplate> pageTemplates(String category, String style, Integer status, Page<ClothTemplate> page) {
        LambdaQueryWrapper<ClothTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) {
            wrapper.eq(ClothTemplate::getCategory, category);
        }
        if (StringUtils.hasText(style)) {
            wrapper.like(ClothTemplate::getStyleTags, style);
        }
        if (status != null) {
            wrapper.eq(ClothTemplate::getStatus, status);
        }
        wrapper.orderByDesc(ClothTemplate::getSort).orderByDesc(ClothTemplate::getId);
        return clothTemplateMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ClothTemplate> recommend(Integer limit) {
        int realLimit = limit == null || limit <= 0 ? 8 : limit;
        LambdaQueryWrapper<ClothTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClothTemplate::getStatus, 1)
                .orderByDesc(ClothTemplate::getSort)
                .orderByDesc(ClothTemplate::getId)
                .last("limit " + realLimit);
        return clothTemplateMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public ClothTemplate create(ClothTemplate template) {
        clothTemplateMapper.insert(template);
        return template;
    }

    @Override
    @Transactional
    public List<ClothTemplate> createBatch(List<ClothTemplate> templates) {
        if (templates == null || templates.isEmpty()) {
            throw new IllegalArgumentException("template list is empty");
        }
        for (ClothTemplate template : templates) {
            clothTemplateMapper.insert(template);
        }
        return templates;
    }

    @Override
    @Transactional
    public ClothTemplate update(Long id, ClothTemplate template) {
        ClothTemplate existing = clothTemplateMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Template not found");
        }
        template.setId(id);
        clothTemplateMapper.updateById(template);
        return clothTemplateMapper.selectById(id);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Integer status) {
        ClothTemplate existing = clothTemplateMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Template not found");
        }
        ClothTemplate update = new ClothTemplate();
        update.setId(id);
        update.setStatus(status);
        clothTemplateMapper.updateById(update);
    }

    @Override
    public ClothTemplate getById(Long id) {
        return clothTemplateMapper.selectById(id);
    }
}
