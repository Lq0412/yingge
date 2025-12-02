package com.lq.yingge_backend.service;

import com.lq.yingge_backend.model.entity.TryOnTask;

public interface TryOnTaskService {

    TryOnTask submitTask(Long userId, String personImageUrl, String clothImageUrl, String prompt);

    TryOnTask getById(Long taskId);
}
