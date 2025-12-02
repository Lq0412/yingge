package com.lq.yingge_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lq.yingge_backend.annotation.LoginRequired;
import com.lq.yingge_backend.model.entity.TryOnRecord;
import com.lq.yingge_backend.model.vo.TryOnRecordVO;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.service.TryOnRecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "试装记录")
public class TryOnRecordController {

    private final TryOnRecordService tryOnRecordService;

    @LoginRequired
    @GetMapping
    @Operation(summary = "查询试装记录列表", description = "分页查询当前用户试装记录")
    public ResponseEntity<IPage<TryOnRecordVO>> list(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("LOGIN_USER");
        Page<TryOnRecord> mpPage = new Page<>(page, size);
        IPage<TryOnRecord> recordPage = tryOnRecordService.pageByUser(loginUser.getId(), mpPage);
        IPage<TryOnRecordVO> voPage = recordPage.convert(this::toVO);
        return ResponseEntity.ok(voPage);
    }

    @LoginRequired
    @GetMapping("/{id}")
    @Operation(summary = "查询试装记录详情")
    public ResponseEntity<TryOnRecordVO> detail(@PathVariable Long id, HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("LOGIN_USER");
        TryOnRecord record = tryOnRecordService.getById(id);
        if (record == null || !record.getUserId().equals(loginUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toVO(record));
    }

    private TryOnRecordVO toVO(TryOnRecord record) {
        TryOnRecordVO vo = new TryOnRecordVO();
        vo.setId(record.getId());
        vo.setPersonImageUrl(record.getPersonImageUrl());
        vo.setClothImageUrl(record.getClothImageUrl());
        vo.setResultImageUrl(record.getResultImageUrl());
        vo.setStatus(record.getStatus());
        vo.setMessage(record.getMessage());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
