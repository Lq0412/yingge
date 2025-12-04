# Prompt Template Plan

## Goal & Scope
- Provide official cloth templates for quick try-on; users can also upload custom cloth images.
- Submission payload stays: personImageUrl + clothImageUrl (+ maskUrl optional) + prompt(optional) + strength(optional) + aspectRatio(optional).
- Add recommendation block and allow custom prompt overrides.

## Data Model (DB or JSON seed -> DB later)
- cloth_template: id (PK), name, image_url, mask_url (optional), category, style_tags (JSON array), color, fit, status (0=off,1=on,2=gray), sort, lang, created_at, updated_at.
- Optional default prompt fields if desired: prompt, negative_prompt, strength (decimal 0.30-0.90), aspect_ratio (e.g., 3:4).
- Validation: image >=1024px, jpg/png <=5MB; status controls exposure; name unique.

## APIs
- GET /api/cloth/templates?status=1&category=&style=&page=&size=&order=
  - Returns list + pagination; cacheable 5-10 min; invalidate on write.
- GET /api/cloth/recommend (optional v1: same as templates ordered by sort/hot; v2: rule-based on user history/tags).
- POST /api/storage/cloth (upload cloth image to COS), returns imageUrl (+maskUrl if preprocessed); auth: admin/internal.
- POST /api/cloth/templates (create), PUT /api/cloth/templates/{id} (update), PATCH /api/cloth/templates/{id}/status (on/off/gray); auth: admin.
- Try-on submission remains current endpoint; expects clothImageUrl (+maskUrl) from selected template; prompt optional.

## Frontend UX
- Official templates grid with filters (category/style/color) and search; status=on only.
- Recommended block (top of list) using GET /api/cloth/recommend.
- Template card click -> fills clothImageUrl(+maskUrl) into try-on form; user can submit directly.
- Custom prompt toggle: off by default; when on, user edits prompt/negative_prompt; strength default 0.65; aspect_ratio default 3:4.
- Custom upload entry: user can upload cloth image if no suitable template.
- Error/empty states: show fallback text + custom upload path.

## Recommendation (phased)
- Phase 1: sort by manual weight + usage hotness; return top N.
- Phase 2: rule-based: match latest category/style user picked; occasion hints -> pick matching tags (e.g., wedding -> white/soft styles).
- Fallback: random from status=on pool; never return status!=1.

## Prompt Handling
- Default prompt (if user does not type): built from category/style tags; include material and cleanliness; generic negative: "blurry, extra limbs, distorted face, watermark, text".
- Strength clamp: 0.30-0.90; default 0.65; aspect_ratio default 3:4.
- Validate prompt length <=1000 chars; sanitize for sensitive terms.

### Yingge (英歌舞) Presets
- Core structure: keep a stable base prompt (studio lighting, clean background, keep same person) + Yingge-style block (traditional / modern / youth / female warrior) + generic negative prompt.
- Base block example: `full-body portrait of the same person standing, studio lighting, clean plain background, high detail, keep original face and body shape, natural skin tone, no face replacement`.
- Yingge style blocks (can be used as `prompt` fragments in templates):
  - Traditional team member: `Chaoshan Yingge folk dance costume, traditional red and black warrior uniform, bold golden embroidery, wide belt, layered shoulder armor details, dynamic yet ceremonial stage outfit`.
  - Hero leader: `heroic Yingge warrior inspired by Water Margin heroes, heavier armor-like costume, darker red and black palette, metal-like trims, commanding presence, rich historical details`.
  - City remix: `modern streetwear reinterpretation of Chaoshan Yingge costume, bomber jacket with Yingge patterns, sporty pants, sneakers, neon accent colors, fusion of tradition and urban fashion`.
  - Campus collab: `youthful Yingge-inspired casual outfit, varsity jacket with Yingge embroidery, relaxed fit pants, fresh and bright colors, suitable for campus and daily wear`.
  - Female warrior: `female Yingge warrior inspired outfit, light armor dress silhouette, waist-emphasized tailoring, flowing lower part, auspicious red and gold color scheme, elegant yet powerful`.
- Recommended "standard Yingge try-on" full prompt (for official templates / default case):
  - `full-body portrait of the same person standing, studio lighting, clean plain background, high detail, wearing a Chaoshan Yingge folk dance warrior costume, traditional red and black uniform with bold golden embroidery, wide belt, layered shoulder armor, structured sleeves, thick fabric texture, details clearly visible, heroic and energetic pose, cinematic but realistic style, keep original face and body shape, natural skin tone, no face replacement`.
- Recommended negative prompt (can be reused across Yingge templates):
  - `blurry, low resolution, extra limbs, extra hands, extra fingers, distorted face, face replacement, different person, deformed body, cropped head, watermark, logo, text, noisy background, heavy makeup covering face, wrong ethnicity`.

## Seed Data
- Prepare 5-10 official cloth templates with imageUrl + category/style/color + sort; store in data/cloth_templates_seed.json; import on boot/dev.
- If maskUrl available (pre-cut), include to improve compositing quality.

## Rollout Steps
1) Create cloth_template table and seed importer; add cache layer.
2) Implement CRUD + status toggle + upload endpoint (admin only).
3) Implement GET /api/cloth/templates and optional /recommend; wire cache invalidation.
4) Frontend: template grid + recommend block + custom upload + prompt toggle; connect to try-on submission.
5) QA: select template -> submit -> poll status -> verify generated image matches cloth; test empty/failure paths.
6) Docs: update Swagger/Knife4j with examples; add README snippet for prompt defaults and params.

## Ops & Observability
- Log template creates/updates/status changes; trace templateId through try-on requests.
- Metrics: template list latency p95, recommend hit ratio, try-on success rate by template.
- Access control: GET open to users; POST/PUT/PATCH/UPLOAD require admin role/session.

## Current Backend Status
- Added `cloth_template` table (MySQL) with fields above.
- Implemented CRUD/list/recommend endpoints: `/api/cloth/templates`, `/api/cloth/recommend`, admin POST/PUT/PATCH for template management; styleTags 支持数组传参。
- 新增批量导入接口 `/api/cloth/templates/batch`，便于导入种子数据（示例见 docs/cloth_templates_seed.json）。
- `ClothTemplate` entity/mapper/service/controller in codebase; MyBatis-Plus pagination ready。
- Frontend can now pull templates and send `clothImageUrl (+ maskUrl)` plus optional prompt/strength to existing try-on API.

## 当前前端进度
- 今天主要进行前端开发，推进页面和交互相关内容，后续继续完善并与后端接口联调。
