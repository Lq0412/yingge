package com.lq.yingge_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lq.yingge_backend.model.entity.ClothTemplate;
import java.util.List;

public interface ClothTemplateService {

    IPage<ClothTemplate> pageTemplates(String category, String style, Integer status, Page<ClothTemplate> page);

    List<ClothTemplate> recommend(Integer limit);

    ClothTemplate create(ClothTemplate template);

    List<ClothTemplate> createBatch(List<ClothTemplate> templates);

    ClothTemplate update(Long id, ClothTemplate template);

    void changeStatus(Long id, Integer status);

    ClothTemplate getById(Long id);
}
