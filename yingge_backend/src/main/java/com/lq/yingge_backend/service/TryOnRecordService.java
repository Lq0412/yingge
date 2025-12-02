package com.lq.yingge_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lq.yingge_backend.model.entity.TryOnRecord;

public interface TryOnRecordService {

    void saveRecord(TryOnRecord record);

    IPage<TryOnRecord> pageByUser(Long userId, Page<TryOnRecord> page);

    TryOnRecord getById(Long id);
}
