# 英歌舞在线试装前端

一个帮助用户在线试穿英歌舞服饰的前端应用。用户上传人像、选择英歌舞服饰，调用后端 AI 服务生成试装效果图，并可查看历史记录。

## 功能概览
- 人像上传与服装选择：引导用户上传清晰半身照，浏览英歌舞服装素材。
- 试装生成：触发后端 AI 试装接口，展示生成状态与结果图。
- 记录查看：查看近期试装记录与详情，便于回看或再次分享/下载。

## 技术栈
- Vite + Vue 3 + TypeScript
- Vue Router / 状态管理（视项目实际使用）
- Cypress（E2E）、Vitest（单测）、ESLint + Prettier

## 开发环境
- Node.js 18+（建议使用 `nvm`/`fnm` 管理）

## 快速开始
```sh
npm install
npm run dev           # 本地启动
npm run build         # 生产构建
npm run test:unit     # 单元测试（Vitest）
npm run test:e2e:dev  # E2E，基于开发服
npm run lint          # 代码风格检查
```

## 目录说明
- `src/`：业务代码（页面、组件、路由、状态等）
- `public/`：静态资源
- `cypress/`：端到端测试
- `vite.config.ts` / `vitest.config.ts`：构建与测试配置

## 后续迭代提示
- 接入后端鉴权后，将请求携带 Token/Session。
- 若 AI 生成时间较长，前端需有加载/重试/失败提示，并考虑结果轮询或进度显示。
- 上传/预览时注意尺寸与格式校验，避免大文件阻塞。
