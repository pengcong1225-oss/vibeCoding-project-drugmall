# DrugMall 智能体规则

## 1. 智能体选择规则

按开发阶段选择智能体：

| 阶段 | 必须使用 | 可选使用 |
|------|----------|----------|
| 需求规划 | product-strategist | compliance-checker |
| UI设计 | ui-designer | - |
| 前端开发 | frontend-architect | ai-integration-eng |
| 后端开发 | backend-architect | ai-integration-eng |
| 测试 | api-test-pro | performance-expert, compliance-checker |
| 部署运维 | devops-architect | performance-expert |
| 文档分析 | doc-knowledge-reader | - |

**选择原则**：
- 涉及药品信息准确性 → 必须引入 compliance-checker 审查
- 涉及用户隐私/支付安全 → 必须引入 compliance-checker 审查
- 前后端并行开发时，frontend-architect 和 backend-architect 可同时调用
- 测试阶段 api-test-pro 和 performance-expert 可同时调用

## 2. Git 操作强制规则

**所有 git add/commit/push 操作必须通过 devops-architect 智能体执行，禁止主智能体直接操作。**

提交信息遵循 Conventional Commits：
- `feat:` 新功能 | `fix:` 修复 | `docs:` 文档 | `refactor:` 重构 | `test:` 测试 | `chore:` 构建

分支策略：master(生产) → develop(开发) → feature/xxx(功能) | bugfix/xxx(修复) | hotfix/xxx(紧急修复)

**禁止事项**：
- 禁止主智能体直接执行 git add/commit/push
- 禁止提交敏感信息（密码、密钥）
- 禁止提交 node_modules
- 禁止强制推送到 master

## 3. 药品电商合规规则

涉及以下场景时，必须确保合规性：

- **处方药**：必须凭处方购买，需处方审核流程
- **药品经营**：需药品经营许可证、GSP认证
- **实名认证**：用户购药必须实名
- **电子处方**：需对接医院/医生提供电子处方服务
- **数据安全**：用户健康数据加密存储，符合《个人信息保护法》
- **支付安全**：交易数据安全保护

## 4. 文档存储规则

```
docs/
├── 产品方案/           # PRD、产品需求文档
├── 设计文档/           # UI设计规范、设计稿
├── 开发文档/           # 开发规范、配置指南
├── 测试文档/           # 测试用例、测试报告、测试脚本
└── 文档索引.md
```

- 测试用例 → `docs/测试文档/`
- 测试报告命名 → `[端名]测试报告-[日期].md`
- 测试脚本 → `docs/测试文档/scripts/`
- UI设计规范 → `docs/设计文档/UI设计规范-[端名]-v[版本].md`
