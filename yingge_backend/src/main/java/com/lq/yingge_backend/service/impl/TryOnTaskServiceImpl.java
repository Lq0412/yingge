package com.lq.yingge_backend.service.impl;

import com.lq.yingge_backend.mapper.TryOnTaskMapper;
import com.lq.yingge_backend.model.entity.TryOnTask;
import com.lq.yingge_backend.service.TryOnTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TryOnTaskServiceImpl implements TryOnTaskService {

    private final TryOnTaskMapper tryOnTaskMapper;
    private final TryOnTaskAsyncExecutor tryOnTaskAsyncExecutor;

    @Override
    @Transactional
    public TryOnTask submitTask(Long userId, String personImageUrl, String clothImageUrl, String prompt) {
        if (userId == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        if (personImageUrl == null || personImageUrl.isBlank() || clothImageUrl == null || clothImageUrl.isBlank()) {
            throw new IllegalArgumentException("图片地址不能为空");
        }
        TryOnTask task = new TryOnTask();
        task.setUserId(userId);
        task.setPersonImageUrl(personImageUrl);
        task.setClothImageUrl(clothImageUrl);
        task.setPrompt(prompt);
        task.setStatus("pending");
        tryOnTaskMapper.insert(task);
        tryOnTaskAsyncExecutor.processTaskAsync(task.getId());
        return task;
    }

    @Override
    public TryOnTask getById(Long taskId) {
        return tryOnTaskMapper.selectById(taskId);
    }
}
