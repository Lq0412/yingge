package com.lq.yingge_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lq.yingge_backend.mapper.TryOnRecordMapper;
import com.lq.yingge_backend.model.entity.TryOnRecord;
import com.lq.yingge_backend.service.TryOnRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TryOnRecordServiceImpl implements TryOnRecordService {

    private final TryOnRecordMapper tryOnRecordMapper;

    @Override
    public void saveRecord(TryOnRecord record) {
        tryOnRecordMapper.insert(record);
    }

    @Override
    public IPage<TryOnRecord> pageByUser(Long userId, Page<TryOnRecord> page) {
        return tryOnRecordMapper.selectPage(page, new LambdaQueryWrapper<TryOnRecord>()
                .eq(TryOnRecord::getUserId, userId)
                .eq(TryOnRecord::getIsDelete, 0)
                .orderByDesc(TryOnRecord::getId));
    }

    @Override
    public TryOnRecord getById(Long id) {
        return tryOnRecordMapper.selectById(id);
    }
}
