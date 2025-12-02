package com.lq.yingge_backend.controller;

import com.lq.yingge_backend.annotation.LoginRequired;
import com.lq.yingge_backend.model.entity.TryOnTask;
import com.lq.yingge_backend.model.vo.TryOnTaskVO;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.service.TryOnTaskService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "试装任务")
public class TryOnTaskController {

    private final TryOnTaskService tryOnTaskService;

    @LoginRequired
    @PostMapping("/tryon")
    @Operation(summary = "提交试装任务", description = "传入人像/服装 URL 与可选 prompt，后台异步处理")
    public ResponseEntity<TryOnTaskVO> submit(@RequestParam("personUrl") String personUrl,
                                              @RequestParam("clothUrl") String clothUrl,
                                              @RequestParam(value = "prompt", required = false) String prompt,
                                              HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("LOGIN_USER");
        TryOnTask task = tryOnTaskService.submitTask(loginUser.getId(), personUrl, clothUrl, prompt);
        return ResponseEntity.ok(toVO(task));
    }

    @LoginRequired
    @GetMapping("/tryon/{id}")
    @Operation(summary = "查询试装任务状态")
    public ResponseEntity<TryOnTaskVO> detail(@PathVariable("id") Long id, HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("LOGIN_USER");
        TryOnTask task = tryOnTaskService.getById(id);
        if (task == null || !task.getUserId().equals(loginUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toVO(task));
    }

    private TryOnTaskVO toVO(TryOnTask task) {
        TryOnTaskVO vo = new TryOnTaskVO();
        vo.setId(task.getId());
        vo.setPersonImageUrl(task.getPersonImageUrl());
        vo.setClothImageUrl(task.getClothImageUrl());
        vo.setResultImageUrl(task.getResultImageUrl());
        vo.setPrompt(task.getPrompt());
        vo.setStatus(task.getStatus());
        vo.setErrorMsg(task.getErrorMsg());
        vo.setCreateTime(task.getCreateTime());
        vo.setUpdateTime(task.getUpdateTime());
        return vo;
    }
}
