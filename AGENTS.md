# DrugMall 智能体协作规范

## 1. 智能体矩阵（10 个）

### 架构设计

| 智能体 | 职责 | 适用场景 |
|--------|------|----------|
| product-strategist | 需求分析、功能优先级、用户旅程、PRD | 新功能规划、产品方案评审 |
| doc-knowledge-reader | 读取 docs/ 文档，提供业务上下文 | 需求理解、设计还原、历史问题排查 |
| ui-designer | Vue 组件样式、SCSS、设计还原 | 新页面 UI、样式重构 |

### 开发实现

| 智能体 | 职责 | 适用场景 |
|--------|------|----------|
| backend-architect | Spring Boot API、MyBatis Plus 模型、数据库 | 新 API、数据模型、后端优化 |
| frontend-architect | Vue 3 组件、Pinia Store、路由 | 新页面、组件重构、前端优化 |
| ai-integration-eng | 百川大模型、腾讯 IM SDK、prompt 工程 | AI 功能、IM 聊天、AI 导诊 |

### 质量保障

| 智能体 | 职责 | 适用场景 |
|--------|------|----------|
| api-test-pro | 接口契约检查、前后端一致性、边界测试 | 新接口测试、回归测试 |
| compliance-checker | 处方合规、数据隐私、AI 免责、支付安全 | 涉及健康/处方/隐私的功能 |
| performance-expert | 查询优化、渲染性能、包体积 | 性能瓶颈排查、构建优化 |

### 部署运维

| 智能体 | 职责 | 适用场景 |
|--------|------|----------|
| devops-architect | 环境配置、Git 操作、启动部署 | 配置变更、Git 提交、部署 |

## 2. 智能体选择原则

- **涉及处方/隐私/支付** → 必须引入 compliance-checker
- **涉及 AI/IM** → 必须引入 ai-integration-eng
- **需求不确定时** → 先调用 doc-knowledge-reader 查文档，再调用 product-strategist 规划
- **前后端并行开发** → frontend-architect 和 backend-architect 可同时调用
- **测试阶段** → api-test-pro 和 performance-expert 可同时调用
- **UI 开发时** → 可先查 ui-designer 获取样式方案，再由 frontend-architect 实现

## 3. 开发流程

```
需求分析（product-strategist + doc-knowledge-reader）
    ↓
方案设计（backend-architect + frontend-architect + ui-designer + ai-integration-eng）
    ↓
开发实现（backend-architect / frontend-architect / ai-integration-eng）
    ↓
测试验证（api-test-pro + performance-expert + compliance-checker）
    ↓
部署上线（devops-architect）
```

## 4. Git 操作规则

**所有 git add/commit/push 操作必须通过 devops-architect 智能体执行，禁止主智能体直接操作。**

- 提交信息遵循 Conventional Commits：`feat:` / `fix:` / `docs:` / `refactor:` / `test:` / `chore:`
- 分支策略：`master`(生产) → `develop`(开发) → `feature/xxx` | `bugfix/xxx` | `hotfix/xxx`
- **禁止**：主智能体直接执行 Git 操作、提交敏感信息（.env, application.yml）、提交 node_modules、强制推送到 master

## 5. 药品电商合规要点

涉及以下场景必须通过 compliance-checker 审查：

- **处方药**：必须凭处方购买，需处方审核流程（Prescription 状态机）
- **实名认证**：用户购药必须实名（User 实体含 realName/idCard）
- **电子处方**：需对接持证医生（Doctor 含 licenseCode/certificationStatus）
- **AI 诊断**：百川 AI 回复必须含"仅供参考"免责声明
- **数据安全**：用户健康数据加密存储，符合《个人信息保护法》
- **支付安全**：交易数据安全保护

## 6. 技术栈速查

| 层 | 技术 |
|---|------|
| 患者端前端 | Vue 3.4 + TypeScript + Element Plus 2.5 + Pinia + Vite 5 |
| 医生端前端 | Vue 3.4 + TypeScript + Pinia + Vite 5（无 UI 库） |
| 管理后台前端 | Vue 3.4 + TypeScript + Element Plus + Vite 5 |
| 主后端 | Spring Boot 3.2.5 + MyBatis Plus 3.5.5 + MySQL + Redis + Knife4j |
| 管理后端 | Spring Boot 3.2.5 + MyBatis Plus 3.5.5 + MySQL |
| AI | 百川大模型 Baichuan4-Turbo |
| IM | 腾讯云 TIM（Mock/真实双模式） |

## 7. 文档参考

- PRD：`docs/产品方案/PRD-*.md`
- UI 规范：`docs/设计文档/UI设计规范-*.md`
- 开发报告：`docs/开发文档/*.md`
- 测试报告：`docs/测试文档/*.md`

**注意**：docs/ 中的文档可能落后于代码迭代，以实际代码为最终真相源。
