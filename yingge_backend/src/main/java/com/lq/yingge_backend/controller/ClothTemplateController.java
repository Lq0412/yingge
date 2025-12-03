package com.lq.yingge_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lq.yingge_backend.annotation.AdminRequired;
import com.lq.yingge_backend.model.dto.ClothTemplateRequest;
import com.lq.yingge_backend.model.entity.ClothTemplate;
import com.lq.yingge_backend.model.vo.ClothTemplateVO;
import com.lq.yingge_backend.service.ClothTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cloth")
@RequiredArgsConstructor
@Tag(name = "服装模板")
public class ClothTemplateController {

    private static final String DEFAULT_NEGATIVE_PROMPT = "blurry, extra limbs, distorted face, watermark, text";
    private static final BigDecimal DEFAULT_STRENGTH = BigDecimal.valueOf(0.65);
    private static final String DEFAULT_ASPECT_RATIO = "3:4";

    private final ClothTemplateService clothTemplateService;

    @GetMapping("/templates")
    @Operation(summary = "获取服装模板列表", description = "可按分类/风格过滤，默认仅返回上架模板（status=1）")
    public ResponseEntity<IPage<ClothTemplateVO>> list(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String category,
                                                       @RequestParam(required = false) String style,
                                                       @RequestParam(required = false, defaultValue = "1") Integer status) {
        Page<ClothTemplate> mpPage = new Page<>(page, size);
        IPage<ClothTemplate> templatePage = clothTemplateService.pageTemplates(category, style, status, mpPage);
        IPage<ClothTemplateVO> voPage = templatePage.convert(this::toVO);
        return ResponseEntity.ok(voPage);
    }

    @GetMapping("/recommend")
    @Operation(summary = "推荐服装模板", description = "按权重/热度取前 N，仅返回上架模板")
    public ResponseEntity<List<ClothTemplateVO>> recommend(@RequestParam(required = false) Integer limit) {
        List<ClothTemplateVO> list = clothTemplateService.recommend(limit).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @AdminRequired
    @PostMapping("/templates")
    @Operation(summary = "创建服装模板", description = "管理员：必须提供 imageUrl 和 name，可选 category/styleTags/prompt")
    public ResponseEntity<ClothTemplateVO> create(@RequestBody ClothTemplateRequest request) {
        validateRequest(request);
        ClothTemplate template = buildTemplate(request, null);
        ClothTemplate saved = clothTemplateService.create(template);
        return ResponseEntity.ok(toVO(saved));
    }

    @AdminRequired
    @PutMapping("/templates/{id}")
    @Operation(summary = "更新服装模板")
    public ResponseEntity<ClothTemplateVO> update(@PathVariable Long id, @RequestBody ClothTemplateRequest request) {
        validateRequest(request);
        ClothTemplate existing = clothTemplateService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        ClothTemplate merged = buildTemplate(request, existing);
        ClothTemplate updated = clothTemplateService.update(id, merged);
        return ResponseEntity.ok(toVO(updated));
    }

    @AdminRequired
    @PatchMapping("/templates/{id}/status")
    @Operation(summary = "变更模板状态", description = "0=下线，1=上架，2=灰度")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (status == null || status < 0 || status > 2) {
            throw new IllegalArgumentException("status must be 0/1/2");
        }
        clothTemplateService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @AdminRequired
    @PostMapping("/templates/batch")
    @Operation(summary = "批量创建服装模板", description = "管理员：接受模板数组，便于导入种子数据")
    public ResponseEntity<List<ClothTemplateVO>> batchCreate(@RequestBody List<ClothTemplateRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            throw new IllegalArgumentException("request body is empty");
        }
        List<ClothTemplate> list = new ArrayList<>();
        for (ClothTemplateRequest request : requests) {
            validateRequest(request);
            list.add(buildTemplate(request, null));
        }
        List<ClothTemplate> saved = clothTemplateService.createBatch(list);
        List<ClothTemplateVO> voList = saved.stream().map(this::toVO).toList();
        return ResponseEntity.ok(voList);
    }

    private void validateRequest(ClothTemplateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is empty");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("name is required");
        }
        if (!StringUtils.hasText(request.getImageUrl())) {
            throw new IllegalArgumentException("imageUrl is required");
        }
    }

    private ClothTemplate buildTemplate(ClothTemplateRequest request, ClothTemplate base) {
        ClothTemplate template = base == null ? new ClothTemplate() : copy(base);
        template.setName(request.getName());
        template.setImageUrl(request.getImageUrl());
        if (request.getMaskUrl() != null) {
            template.setMaskUrl(request.getMaskUrl());
        }
        if (StringUtils.hasText(request.getCategory())) {
            template.setCategory(request.getCategory());
        }
        if (!CollectionUtils.isEmpty(request.getStyleTags())) {
            template.setStyleTags(toJsonArray(request.getStyleTags()));
        }
        if (StringUtils.hasText(request.getColor())) {
            template.setColor(request.getColor());
        }
        if (StringUtils.hasText(request.getFit())) {
            template.setFit(request.getFit());
        }
        if (request.getPrompt() != null) {
            template.setPrompt(request.getPrompt());
        }
        if (request.getNegativePrompt() != null) {
            template.setNegativePrompt(request.getNegativePrompt());
        }
        if (request.getStrength() != null) {
            template.setStrength(request.getStrength());
        }
        if (StringUtils.hasText(request.getAspectRatio())) {
            template.setAspectRatio(request.getAspectRatio());
        }
        if (request.getStatus() != null) {
            template.setStatus(request.getStatus());
        } else if (template.getStatus() == null) {
            template.setStatus(1);
        }
        if (request.getSort() != null) {
            template.setSort(request.getSort());
        }
        if (StringUtils.hasText(request.getLang())) {
            template.setLang(request.getLang());
        } else if (!StringUtils.hasText(template.getLang())) {
            template.setLang("zh-CN");
        }
        if (!StringUtils.hasText(template.getNegativePrompt())) {
            template.setNegativePrompt(DEFAULT_NEGATIVE_PROMPT);
        }
        if (template.getStrength() == null) {
            template.setStrength(DEFAULT_STRENGTH);
        }
        if (!StringUtils.hasText(template.getAspectRatio())) {
            template.setAspectRatio(DEFAULT_ASPECT_RATIO);
        }
        return template;
    }

    private ClothTemplate copy(ClothTemplate source) {
        ClothTemplate t = new ClothTemplate();
        t.setId(source.getId());
        t.setName(source.getName());
        t.setImageUrl(source.getImageUrl());
        t.setMaskUrl(source.getMaskUrl());
        t.setCategory(source.getCategory());
        t.setStyleTags(source.getStyleTags());
        t.setColor(source.getColor());
        t.setFit(source.getFit());
        t.setStatus(source.getStatus());
        t.setSort(source.getSort());
        t.setLang(source.getLang());
        t.setPrompt(source.getPrompt());
        t.setNegativePrompt(source.getNegativePrompt());
        t.setStrength(source.getStrength());
        t.setAspectRatio(source.getAspectRatio());
        t.setCreateTime(source.getCreateTime());
        t.setUpdateTime(source.getUpdateTime());
        return t;
    }

    private String toJsonArray(List<String> tags) {
        List<String> cleaned = tags.stream()
                .map(tag -> tag == null ? null : tag.trim())
                .filter(StringUtils::hasText)
                .toList();
        if (cleaned.isEmpty()) {
            return null;
        }
        return cleaned.stream()
                .map(tag -> "\"" + tag.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    private ClothTemplateVO toVO(ClothTemplate template) {
        ClothTemplateVO vo = new ClothTemplateVO();
        vo.setId(template.getId());
        vo.setName(template.getName());
        vo.setImageUrl(template.getImageUrl());
        vo.setMaskUrl(template.getMaskUrl());
        vo.setCategory(template.getCategory());
        vo.setStyleTags(template.getStyleTags());
        vo.setColor(template.getColor());
        vo.setFit(template.getFit());
        vo.setStatus(template.getStatus());
        vo.setSort(template.getSort());
        vo.setLang(template.getLang());
        vo.setPrompt(template.getPrompt());
        vo.setNegativePrompt(template.getNegativePrompt());
        vo.setStrength(template.getStrength());
        vo.setAspectRatio(template.getAspectRatio());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }
}
