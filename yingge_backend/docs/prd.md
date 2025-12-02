# 英歌舞在线试装平台 PRD（MVP）

## 1. 文档信息
- 版本：v1.0（MVP）
- 更新时间：2025-12-02
- 适用范围：前端 / 后端 / 测试 / 运营

## 2. 背景与目标
- 英歌舞服饰线下试穿成本高，线上难以感知上身效果。
- 目标：提供“上传个人照片 + 选择英歌舞服装 = 获取试装效果图”的在线体验，验证兴趣并收集反馈。

## 3. 目标用户与价值
- 普通用户/爱好者：便捷体验造型，提升互动。
- 演出队伍/社团：预览造型，辅助排练与设计。
- 商家/运营方：推广服饰与配饰，降低沟通与退货成本。

## 4. 使用场景
- 购买/租赁前预览；主题活动互动；商家示例展示。

## 5. 核心指标（MVP）
- 试装请求成功率 ≥ 90%（不含外部 AI 不可用）。
- 首次全流程 ≤ 5 分钟；单次生成 ≤ 10 秒或给出进度提示。
- 结果可用性达到“可上线验证”。

## 6. 功能需求
### 6.1 账号与会话
- 手机/邮箱 + 密码注册、登录；Session 鉴权。

### 6.2 图片上传
- 上传 1 张人像/服装图：jpg/png，≤ 5MB。
- 存储：对象存储 COS，数据库仅存 URL、类型/归属/时间。

### 6.3 英歌舞服装素材
- 运营/商家预置上传（后续支持分类/管理）。

### 6.4 AI 试装（当前）
- 输入：人像 URL + 服装 URL + 可选 prompt。
- 后端异步调用火山 Ark SDK 生成结果，下载并上传到 COS，返回结果 URL。
- 任务状态：pending/processing/done/failed。

### 6.5 试装记录
- 字段：用户、人像 URL、服装 URL、结果 URL、状态、错误、时间。
- 提供列表（倒序）与详情；暂不删除。

### 6.6 结果获取
- 查看与下载结果图；结果链接受鉴权或签名保护。

## 7. 非功能需求
- 性能：生成 ≤ 10 秒；列表/详情 p95 ≤ 1 秒。
- 可靠性：外部 AI/COS 不可用时友好降级；关键日志记录。
- 安全：密码加密；仅登录用户可访问自身资源；图片仅用于试装。
- 合规：隐私声明；后续支持图片删除/过期策略。

## 8. 数据与接口概述
- 实体
  - User：id、account、passwordHash、role、时间、isDelete。
  - TryOnTask：id、userId、personImageUrl、clothImageUrl、resultImageUrl、prompt、status、errorMsg、时间。
  - TryOnRecord：id、userId、personImageUrl、clothImageUrl、resultImageUrl、status、message、时间、isDelete。
- 存储：业务数据存 MySQL；图片存 COS，系统仅保存 URL。
- API：RESTful；Swagger/Knife4j 提供接口说明。

## 9. 外部依赖
- 对象存储：腾讯云 COS。
- AI：火山 Ark SDK（试装调用）；Seedream 配置预留。
- 基础设施：MySQL、日志/监控、鉴权（Session）。

## 10. 技术栈（当前）
- 后端：Spring Boot 3.5.x + Java 21，MyBatis-Plus，异步线程池；Knife4j/Swagger。
- 前端：Vue（独立仓库）。
- 依赖：BCrypt、MySQL 驱动、COS SDK、火山 Ark SDK。

## 11. 版本规划
- 当前：注册/登录 → 上传 → 提交试装任务（异步）→ 轮询 → 记录查询。
- 后续：prompt 模板枚举（英歌舞风格）、更多服装分类与标签、重试/批量生成、结果分享与社交、商家后台与数据看板、与电商/活动页的深度接入。
