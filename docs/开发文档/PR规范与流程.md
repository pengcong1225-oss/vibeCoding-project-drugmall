# DrugMall PR（Pull Request）规范与流程

## 概述

本文档定义DrugMall项目中Pull Request的规范、流程和最佳实践，确保代码质量、评审效率和团队协作。

---

## 1. PR类型与分类

### 1.1 PR类型标签

| 类型 | 标签 | 说明 | 适用场景 |
|------|------|------|----------|
| **功能开发** | `feature` | 新功能开发 | 新增门店入驻、问诊购药等功能 |
| **缺陷修复** | `bugfix` | 修复Bug | 修复处方识别失败、医保支付异常等问题 |
| **性能优化** | `performance` | 性能提升 | 优化药品搜索速度、订单处理能力 |
| **代码重构** | `refactor` | 代码重构 | 重构用户认证、订单管理模块 |
| **文档更新** | `documentation` | 文档更新 | 更新API文档、产品说明书 |
| **安全加固** | `security` | 安全修复 | 修复数据泄露、越权访问等安全漏洞 |
| **依赖更新** | `dependency` | 依赖升级 | 升级Spring Boot、Vue等框架版本 |

### 1.2 PR优先级标签

| 优先级 | 标签 | 响应时间 | 适用场景 |
|--------|------|----------|----------|
| **紧急** | `priority-critical` | 2小时内 | 生产环境严重故障、安全漏洞 |
| **高** | `priority-high` | 24小时内 | 核心功能缺陷、性能瓶颈 |
| **中** | `priority-medium` | 72小时内 | 一般功能优化、技术债务 |
| **低** | `priority-low` | 1周内 | 代码清理、文档完善 |

### 1.3 功能模块标签

| 模块 | 标签 | 负责智能体 |
|------|------|-----------|
| **门店入驻** | `module-store` | backend-architect + frontend-architect |
| **问诊购药** | `module-consultation` | backend-architect + frontend-architect + ai-integration-eng |
| **处方找药** | `module-prescription` | backend-architect + frontend-architect + ai-integration-eng |
| **自取配送** | `module-delivery` | backend-architect + frontend-architect |
| **医保结算** | `module-insurance` | backend-architect + frontend-architect + compliance-checker |
| **用户中心** | `module-user` | backend-architect + frontend-architect |
| **商品管理** | `module-product` | backend-architect + frontend-architect |
| **订单管理** | `module-order` | backend-architect + frontend-architect |
| **支付系统** | `module-payment` | backend-architect + frontend-architect + compliance-checker |
| **AI服务** | `module-ai` | ai-integration-eng |
| **基础设施** | `module-infra` | devops-architect + backend-architect |

---

## 2. PR创建规范

### 2.1 PR标题格式

```
[<类型>] <模块>: <简要描述>
```

**示例**：
```
[feature] 门店入驻: 实现药店资质上传和审核流程
[bugfix] 医保结算: 修复医保支付回调处理异常
[performance] 处方找药: 优化处方识别算法响应速度
[refactor] 订单管理: 重构订单状态机实现
[security] 用户中心: 修复JWT令牌验证漏洞
```

### 2.2 PR描述模板

```markdown
## 变更类型
- [ ] 新功能 (feature)
- [ ] Bug修复 (bugfix)
- [ ] 性能优化 (performance)
- [ ] 代码重构 (refactor)
- [ ] 文档更新 (documentation)
- [ ] 安全加固 (security)
- [ ] 依赖更新 (dependency)

## 关联Issue
Closes #<issue_number>
Related to #<issue_number>

## 变更描述
### 背景
<!-- 描述为什么需要这个变更 -->

### 实现内容
<!-- 详细描述实现的内容 -->

### 技术方案
<!-- 描述采用的技术方案和架构设计 -->

## 影响范围
- [ ] 前端界面
- [ ] 后端API
- [ ] 数据库结构
- [ ] 配置文件
- [ ] 依赖库
- [ ] 部署脚本

## 测试情况
### 单元测试
- [ ] 新增/修改单元测试
- [ ] 单元测试覆盖率 > 80%

### 集成测试
- [ ] 通过本地集成测试
- [ ] 通过CI自动化测试

### 手动测试
- [ ] 本地开发环境验证
- [ ] 测试环境验证

## 安全审查
- [ ] 无敏感信息泄露
- [ ] 输入参数已校验
- [ ] 权限控制已验证
- [ ] 日志脱敏处理

## 回滚方案
<!-- 描述如何回滚本次变更 -->

## 部署说明
<!-- 特殊的部署注意事项 -->

## 审查检查清单
### 代码质量
- [ ] 代码风格符合规范
- [ ] 命名清晰、注释完整
- [ ] 无冗余代码和调试代码
- [ ] 错误处理完善

### 架构设计
- [ ] 符合项目架构规范
- [ ] 模块划分合理
- [ ] 接口设计规范
- [ ] 扩展性考虑充分

### 性能考虑
- [ ] 无明显性能瓶颈
- [ ] 大数据量处理考虑
- [ ] 缓存策略合理
- [ ] 资源释放正确
```

### 2.3 PR大小规范

| 类别 | 代码行数限制 | 审查时间 | 适用场景 |
|------|-------------|----------|----------|
| **小型PR** | < 200行 | 15-30分钟 | Bug修复、配置修改、文档更新 |
| **中型PR** | 200-500行 | 30-60分钟 | 功能增强、接口调整、组件开发 |
| **大型PR** | 500-1000行 | 1-2小时 | 新功能开发、模块重构、架构调整 |
| **特大型PR** | > 1000行 | 分多次审查 | 建议拆分为多个PR |

**拆分建议**：
- 按功能模块拆分（如门店入驻拆分为：申请表单、资质上传、审核流程）
- 按前后端拆分（前端UI + 后端API分别提PR）
- 按业务层次拆分（数据层、业务层、表现层）

---

## 3. PR审查流程

### 3.1 审查角色定义

| 角色 | 职责 | 审查重点 |
|------|------|----------|
| **作者（Author）** | 提交PR，响应反馈 | 确保PR质量，及时修复问题 |
| **审查员（Reviewer）** | 代码审查，提出意见 | 代码质量、架构设计、业务逻辑 |
| **合规检查员** | 合规性审查 | 法律法规、安全规范、数据保护 |
| **维护者（Maintainer）** | 最终审核，合并代码 | 整体质量把控，版本控制 |

### 3.2 审查流程图

```
┌─────────────────┐
│   PR 提交       │
└────────┬────────┘
         ▼
┌─────────────────┐
│ 自动检查        │
│ (CI/CD、Lint)   │
└────────┬────────┘
         ▼
┌─────────────────┐     ┌─────────────┐
│ 自动检查通过？   │──否──▶│ 作者修复    │
└────────┬────────┘     └─────────────┘
         │是
         ▼
┌─────────────────┐
│ 分配审查员      │
└────────┬────────┘
         ▼
┌─────────────────┐     ┌─────────────┐
│ 审查通过？       │──否──▶│ 作者修复    │
└────────┬────────┘     └─────────────┘
         │是
         ▼
┌─────────────────┐     ┌─────────────┐
│ 合规检查通过？   │──否──▶│ 作者修复    │
└────────┬────────┘     └─────────────┘
         │是
         ▼
┌─────────────────┐
│ 维护者审核     │
└────────┬────────┘
         ▼
┌─────────────────┐
│ 合并到主分支     │
└─────────────────┘
```

### 3.3 审查检查清单

#### 代码质量检查

- [ ] **代码风格**：符合项目编码规范
- [ ] **命名规范**：变量、函数、类名清晰易懂
- [ ] **代码注释**：关键逻辑有适当注释
- [ ] **代码复用**：避免重复代码，提取公共方法
- [ ] **错误处理**：完善的异常处理和错误提示
- [ ] **日志记录**：关键操作有日志记录，敏感信息脱敏

#### 架构设计检查

- [ ] **模块划分**：职责清晰，高内聚低耦合
- [ ] **接口设计**：RESTful规范，参数校验完善
- [ ] **数据库设计**：索引合理，避免N+1查询
- [ ] **缓存策略**：热点数据缓存，缓存一致性
- [ ] **扩展性**：预留扩展接口，避免硬编码

#### 业务逻辑检查

- [ ] **功能完整性**：实现需求文档的所有功能点
- [ ] **业务正确性**：符合业务流程和规则
- [ ] **边界处理**：空值、越界、异常输入处理
- [ ] **数据一致性**：事务处理，数据完整性
- [ ] **并发处理**：线程安全，锁机制合理

#### 安全合规检查

- [ ] **身份认证**：JWT/OAuth实现正确，Token安全
- [ ] **权限控制**：RBAC权限验证，防止越权访问
- [ ] **输入校验**：SQL注入、XSS、CSRF防护
- [ ] **敏感数据**：密码加密存储，PII数据脱敏
- [ ] **数据传输**：HTTPS传输，敏感操作二次确认
- [ ] **合规要求**：符合药品电商法规、医保政策

#### 性能优化检查

- [ ] **响应时间**：API响应时间符合SLA要求
- [ ] **资源使用**：CPU、内存、数据库连接合理使用
- [ ] **批量处理**：大数据量分批处理，避免OOM
- [ ] **异步处理**：耗时操作异步执行，消息队列
- [ ] **前端优化**：资源压缩、懒加载、CDN加速

### 3.4 审查反馈规范

#### 反馈分类标签

| 标签 | 含义 | 处理优先级 | 示例 |
|------|------|-----------|------|
| **blocking** | 阻塞性问题，必须修复 | 最高 | 安全漏洞、功能缺陷、编译错误 |
| **major** | 重要问题，建议修复 | 高 | 性能瓶颈、代码重复、逻辑复杂 |
| **minor** | 次要问题，可选修复 | 中 | 命名不规范、缺少注释 |
| **nitpick** | 挑剔性问题，可忽略 | 低 | 代码格式、空格使用 |
| **question** | 疑问，需要澄清 | - | 业务逻辑不理解、实现方案疑问 |
| **suggestion** | 建议，非强制 | - | 更好的实现方式、优化建议 |

#### 反馈格式规范

```markdown
**<标签>**: <问题描述>

<详细说明>

**建议方案**:
```代码示例```

**参考链接**: [链接描述](URL)
```

**示例**：
```markdown
**[blocking]**: 用户密码明文存储，存在严重安全隐患

当前实现将用户密码以明文形式存储在数据库中，不符合安全规范，一旦数据库泄露将导致用户账户信息完全暴露。

**建议方案**:
```java
// 使用BCrypt进行密码加密
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(plainPassword);

// 验证密码
boolean matches = encoder.matches(plainPassword, hashedPassword);
```

**参考链接**: [Spring Security Password Encoding](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
```

### 3.5 审查时间规范

| PR大小 | 首次响应时间 | 完成审查时间 | 作者修复时间 |
|--------|-------------|-------------|-------------|
| 小型PR | 2小时内 | 24小时内 | 4小时内 |
| 中型PR | 4小时内 | 48小时内 | 8小时内 |
| 大型PR | 8小时内 | 72小时内 | 24小时内 |

**说明**：
- 工作时间内计算（9:00-18:00）
- 紧急PR（priority-critical）优先处理
- 跨团队PR需提前沟通协调

---

## 4. PR合并规范

### 4.1 合并条件

PR必须满足以下条件才能合并：

- [ ] **审查通过**：至少2名审查员 approving review
- [ ] **合规检查通过**：compliance-checker 审查通过
- [ ] **CI/CD通过**：所有自动化测试、代码检查通过
- [ ] **无冲突**：与目标分支无代码冲突
- [ ] **阻塞问题修复**：所有blocking标签问题已修复
- [ ] **审批完成**：维护者（Maintainer）最终审批

### 4.2 合并方式

#### Squash Merge（推荐）

**适用场景**：
- 功能开发完成合并到开发分支
- 小型修复合并
- 保持提交历史整洁

**提交信息格式**：
```
<类型>(<模块>): <描述>

<详细描述>

Closes #<issue_number>
```

**示例**：
```
feat(门店入驻): 实现药店资质上传和审核流程

- 添加药店入驻申请表单页面
- 实现营业执照、药品经营许可证上传功能
- 开发后台审核工作流，支持多级审核
- 集成OCR识别，自动提取资质信息
- 添加审核进度通知（短信/邮件/站内信）

Closes #123
```

#### Rebase Merge

**适用场景**：
- 长期功能分支同步主分支更新
- 保持线性提交历史

**注意事项**：
- 仅用于开发过程中，不用于最终合并
- 需要确保每个提交都是可工作的状态

#### Merge Commit

**适用场景**：
- 发布分支合并（release → main）
- 需要保留完整分支历史

**提交信息格式**：
```
Merge branch '<source_branch>' into <target_branch>

<描述>

Related PR: #<pr_number>
```

### 4.3 合并后操作

1. **删除源分支**：合并完成后删除功能分支
2. **更新文档**：更新CHANGELOG、版本说明
3. **关闭Issue**：关联的Issue自动关闭
4. **通知团队**：在团队频道通知关键变更
5. **部署监控**：监控生产环境，确保无异常

---

## 5. 特殊场景处理

### 5.1 紧急修复（Hotfix）

**触发条件**：
- 生产环境严重故障
- 安全漏洞需要立即修复
- 核心功能完全不可用

**流程**：
1. 从 `main` 分支创建 `hotfix/<问题描述>` 分支
2. 快速修复问题，测试验证
3. 提PR到 `main` 分支，标记 `priority-critical`
4. 维护者立即审查，快速合并
5. 同步修复到 `develop` 分支
6. 紧急部署到生产环境

**PR模板**：
```markdown
## 🚨 紧急修复

**问题描述**: <简要描述>

**影响范围**: <影响的用户/功能>

**修复方案**: 
<详细描述>

**测试验证**:
- [ ] 本地测试通过
- [ ] 测试环境验证通过

**回滚方案**:
<如何回滚>

**发布后监控**:
- [ ] 错误日志监控
- [ ] 性能指标监控
- [ ] 用户反馈收集
```

### 5.2 回滚（Rollback）

**触发条件**：
- 新版本上线后出现严重问题
- 需要立即恢复到上一版本

**流程**：
1. 确定回滚版本（上一稳定版本）
2. 在版本控制系统中标记回滚
3. 执行自动化回滚脚本
4. 验证回滚结果
5. 通知相关人员
6. 记录回滚原因，安排问题修复

### 5.3 跨团队协作PR

**适用场景**：
- 需要多个团队合作开发的功能
- 涉及多个模块的架构调整

**流程**：
1. **前期沟通**：相关团队技术负责人召开会议，确定方案
2. **接口约定**：定义清晰的接口契约，使用OpenAPI规范
3. **并行开发**：各团队在自己的分支上开发
4. **集成测试**：在集成环境进行端到端测试
5. **联合审查**：各团队代表参与代码审查
6. **协调上线**：制定统一的上线计划

---

## 6. PR工具与自动化

### 6.1 PR模板配置

在项目根目录创建 `.github/pull_request_template.md`：

```markdown
## 变更类型
<!-- 请选择一项并在 [ ] 中打 x -->
- [ ] 🆕 新功能 (feature)
- [ ] 🐛 Bug修复 (bugfix)
- [ ] ⚡ 性能优化 (performance)
- [ ] ♻️ 代码重构 (refactor)
- [ ] 📝 文档更新 (documentation)
- [ ] 🔒 安全加固 (security)
- [ ] 📦 依赖更新 (dependency)

## 关联Issue
<!-- 关联的Issue编号，使用 Closes/Fixes/Related to 关键字 -->
Closes #
Related to #

## 变更描述
### 背景
<!-- 描述为什么需要这个变更，解决了什么问题 -->

### 实现内容
<!-- 详细描述实现的内容，包括关键设计决策 -->

### 技术方案
<!-- 描述采用的技术方案、架构设计、关键算法 -->

## 影响范围
<!-- 请勾选受影响的范围 -->
- [ ] 📱 前端界面
- [ ] 🔌 后端API
- [ ] 💾 数据库结构
- [ ] ⚙️ 配置文件
- [ ] 📚 依赖库
- [ ] 🚀 部署脚本

## 测试情况
### 单元测试
- [ ] ✅ 新增/修改单元测试
- [ ] 📊 单元测试覆盖率 > 80%

### 集成测试
- [ ] ✅ 通过本地集成测试
- [ ] 🤖 通过CI自动化测试

### 手动测试
- [ ] 🖥️ 本地开发环境验证
- [ ] 🧪 测试环境验证

## 安全审查
- [ ] 🔐 无敏感信息泄露
- [ ] ✅ 输入参数已校验
- [ ] 🛡️ 权限控制已验证
- [ ] 📝 日志脱敏处理

## 回滚方案
<!-- 描述如何回滚本次变更，包括数据库回滚、配置恢复等 -->

## 部署说明
<!-- 特殊的部署注意事项，如配置变更、数据迁移等 -->

## 审查检查清单
### 代码质量
- [ ] 🎨 代码风格符合规范
- [ ] 🏷️ 命名清晰、注释完整
- [ ] 🗑️ 无冗余代码和调试代码
- [ ] ⚠️ 错误处理完善

### 架构设计
- [ ] 🏗️ 符合项目架构规范
- [ ] 📦 模块划分合理
- [ ] 🔌 接口设计规范
- [ ] 📈 扩展性考虑充分

### 性能考虑
- [ ] ⚡ 无明显性能瓶颈
- [ ] 📊 大数据量处理考虑
- [ ] 💾 缓存策略合理
- [ ] 🔄 资源释放正确
```

### 6.2 CI/CD流水线配置

```yaml
# .github/workflows/pr-check.yml
name: PR Check

on:
  pull_request:
    branches: [main, develop]

jobs:
  # 代码检查
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: npm ci
      - name: Run ESLint
        run: npm run lint
      - name: Run Prettier check
        run: npm run format:check

  # 单元测试
  unit-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run Maven test
        run: mvn test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload coverage
        uses: codecov/codecov-action@v3

  # 集成测试
  integration-test:
    runs-on: ubuntu-latest
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
        ports:
          - 3306:3306
      redis:
        image: redis:7.0
        ports:
          - 6379:6379
    steps:
      - uses: actions/checkout@v3
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run integration tests
        run: mvn verify -P integration-test

  # 安全扫描
  security-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run OWASP Dependency Check
        uses: dependency-check/Dependency-Check_Action@main
        with:
          project: 'drugmall'
          path: '.'
          format: 'ALL'
      - name: Upload results
        uses: actions/upload-artifact@v3
        with:
          name: dependency-check-report
          path: reports/

  # 代码质量分析
  code-quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
        with:
          fetch-depth: 0
      - name: SonarQube Scan
        uses: sonarqube-quality-gate-action@master
        with:
          host: ${{ secrets.SONAR_HOST }}
          token: ${{ secrets.SONAR_TOKEN }}
          projectKey: drugmall

  # PR大小检查
  pr-size-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Check PR size
        uses: codelytv/pr-size-checker@v1
        with:
          max_lines_changed: 500
          warning_lines_changed: 300
          message: 'PR大小超过限制，请考虑拆分'

  # 依赖检查
  dependency-review:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Dependency Review
        uses: actions/dependency-review-action@v3
        with:
          fail-on-severity: high

  # 自动化审查
  auto-review:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: AI Code Review
        uses: coderabbitai/ai-pr-reviewer@main
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
        with:
          debug: false
          review_simple_changes: false
          review_comment_lgtm: false
```

### 6.3 自动化PR标签

```yaml
# .github/workflows/pr-labeler.yml
name: PR Labeler

on:
  pull_request:
    types: [opened, edited, synchronize]

jobs:
  label:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      # 根据分支名自动打标签
      - name: Label by branch
        uses: actions/labeler@v4
        with:
          configuration-path: .github/labeler.yml
      
      # 根据PR标题自动打标签
      - name: Label by title
        uses: github/issue-labeler@v3
        with:
          repo-token: ${{ secrets.GITHUB_TOKEN }}
          configuration-path: .github/title-labeler.yml
          enable-versioned-regex: 0
      
      # 根据修改文件自动打标签
      - name: Label by files
        uses: actions/labeler@v4
        with:
          repo-token: ${{ secrets.GITHUB_TOKEN }}
          sync-labels: true
```

```yaml
# .github/labeler.yml
# 模块标签
module-store:
  - 'src/**/store/**/*'
  - 'src/**/merchant/**/*'

module-consultation:
  - 'src/**/consultation/**/*'
  - 'src/**/doctor/**/*'

module-prescription:
  - 'src/**/prescription/**/*'
  - 'src/**/ocr/**/*'

module-delivery:
  - 'src/**/delivery/**/*'
  - 'src/**/logistics/**/*'

module-insurance:
  - 'src/**/insurance/**/*'
  - 'src/**/medicare/**/*'

module-payment:
  - 'src/**/payment/**/*'
  - 'src/**/pay/**/*'

module-order:
  - 'src/**/order/**/*'

module-user:
  - 'src/**/user/**/*'
  - 'src/**/member/**/*'

module-product:
  - 'src/**/product/**/*'
  - 'src/**/medicine/**/*'
  - 'src/**/goods/**/*'

module-ai:
  - 'src/**/ai/**/*'
  - 'src/**/intelligence/**/*'
  - 'src/**/recommendation/**/*'

module-infra:
  - 'src/**/config/**/*'
  - 'src/**/common/**/*'
  - 'src/**/infrastructure/**/*'
  - 'infra/**/*'
  - 'deploy/**/*'

# 类型标签
documentation:
  - 'docs/**/*'
  - '**/*.md'
  - 'README*'
  - 'CHANGELOG*'

frontend:
  - 'web/**/*'
  - 'admin/**/*'
  - 'h5/**/*'
  - 'mini-app/**/*'
  - '**/*.vue'
  - '**/*.tsx'
  - '**/*.ts'
  - '**/*.css'
  - '**/*.less'
  - '**/*.scss'

backend:
  - 'server/**/*'
  - 'service/**/*'
  - 'api/**/*'
  - '**/*.java'
  - '**/*.kt'
  - '**/*.go'
  - '**/*.py'
  - 'pom.xml'
  - 'build.gradle'

database:
  - 'db/**/*'
  - 'migration/**/*'
  - '**/*.sql'
  - '**/*.migration'

test:
  - 'test/**/*'
  - '**/*.spec.js'
  - '**/*.test.js'
  - '**/*.spec.ts'
  - '**/*.test.ts'
  - '**/*Test.java'
  - '**/*IT.java'

dependency:
  - 'package.json'
  - 'package-lock.json'
  - 'yarn.lock'
  - 'pom.xml'
  - 'build.gradle'
  - 'go.mod'
  - 'go.sum'
  - 'requirements.txt'
  - 'Pipfile'
  - 'poetry.lock'

ci-cd:
  - '.github/**/*'
  - '.gitlab-ci.yml'
  - 'Jenkinsfile'
  - 'Dockerfile'
  - 'docker-compose.yml'
  - 'k8s/**/*'
  - 'helm/**/*'
```

---

## 7. 附录

### 7.1 常用命令速查

```bash
# 创建功能分支
git checkout -b feature/门店入驻-资质上传

# 提交代码
git add .
git commit -m "feat(门店入驻): 实现药店资质上传功能

- 添加资质上传组件，支持多文件上传
- 集成阿里云OSS存储
- 添加图片压缩和格式校验

Closes #123"

# 推送到远程
git push origin feature/门店入驻-资质上传

# 同步主分支更新
git checkout main
git pull origin main
git checkout feature/门店入驻-资质上传
git rebase main

# 解决冲突后继续rebase
git add .
git rebase --continue

# 创建PR（使用GitHub CLI）
gh pr create --title "[feature] 门店入驻: 实现药店资质上传功能" \
             --body-file .github/pr_body_template.md \
             --label "feature,module-store,priority-medium" \
             --reviewer "user1,user2"

# 查看PR状态
gh pr view
gh pr checks

# 合并PR
gh pr merge --squash --subject "feat(门店入驻): 实现药店资质上传功能" \
            --body "Closes #123"
```

### 7.2 提交信息规范

#### 格式

```
<类型>(<范围>): <简短描述>

<详细描述>

<页脚信息>
```

#### 类型说明

| 类型 | 含义 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(门店入驻): 添加药店资质上传功能` |
| `fix` | Bug修复 | `fix(医保结算): 修复医保支付回调异常` |
| `docs` | 文档更新 | `docs(README): 更新项目部署说明` |
| `style` | 代码格式 | `style(前端): 统一代码缩进` |
| `refactor` | 代码重构 | `refactor(订单): 重构订单状态机` |
| `perf` | 性能优化 | `perf(搜索): 优化药品搜索响应速度` |
| `test` | 测试相关 | `test(单元测试): 添加订单服务测试` |
| `chore` | 构建/工具 | `chore(构建): 升级Spring Boot版本` |
| `ci` | CI/CD相关 | `ci(GitHub Actions): 添加代码质量检查` |
| `revert` | 回滚 | `revert: 回滚"feat: 添加新功能"` |
| `security` | 安全修复 | `security(认证): 修复JWT令牌验证漏洞` |

#### 范围说明

范围标识修改的模块或组件：

```
门店入驻: store, merchant
问诊购药: consultation, doctor
处方找药: prescription, ocr
自取配送: delivery, logistics
医保结算: insurance, medicare
订单管理: order
用户中心: user, member
商品管理: product, medicine
支付系统: payment
AI服务: ai, recommendation
基础设施: infra, common
```

#### 完整示例

```
feat(门店入驻): 实现药店资质上传和审核流程

- 添加药店资质上传页面，支持营业执照、药品经营许可证上传
- 集成阿里云OSS存储，实现图片压缩和格式校验
- 开发后台审核工作流，支持初审、复审多级审核
- 集成OCR识别服务，自动提取资质信息
- 添加审核进度通知（短信/邮件/站内信）
- 编写单元测试，覆盖率达85%

审核流程：
1. 药店提交资质申请
2. 系统自动OCR识别
3. 运营人员初审（1工作日）
4. 合规部门复审（1工作日）
5. 审核结果通知

Closes #123
Related to #456

BREAKING CHANGE: 药店入驻接口v1版本已废弃，请使用v2版本
```

---

## 8. 工具与集成

### 8.1 推荐的PR工具

| 工具 | 用途 | 链接 |
|------|------|------|
| **GitHub CLI** | 命令行操作PR | https://cli.github.com/ |
| **GitHub Desktop** | 图形化Git操作 | https://desktop.github.com/ |
| **SourceTree** | 高级Git客户端 | https://www.sourcetreeapp.com/ |
| **GitKraken** | 跨平台Git客户端 | https://www.gitkraken.com/ |
| **CodeRabbit** | AI代码审查 | https://coderabbit.ai/ |
| **SonarCloud** | 代码质量分析 | https://sonarcloud.io/ |
| **Codecov** | 覆盖率分析 | https://about.codecov.io/ |

### 8.2 浏览器扩展

| 扩展 | 功能 | 链接 |
|------|------|------|
| **Refined GitHub** | 增强GitHub界面 | https://github.com/refined-github/refined-github |
| **GitHub Dark Theme** | 暗黑主题 | https://github.com/poychang/github-dark-theme |
| **OctoLinker** | 代码跳转 | https://octolinker.github.io/ |
| **Sourcegraph** | 代码搜索 | https://sourcegraph.com/ |

---

## 9. 常见问题（FAQ）

### Q1: PR审查时间太长怎么办？

**A**: 
1. 拆分PR，减少审查负担
2. 提前与审查员沟通，预约审查时间
3. 使用自动审查工具，减少人工工作量
4. 建立审查轮值制度，确保及时响应

### Q2: 审查意见冲突怎么处理？

**A**:
1. 保持技术讨论的专业性，对事不对人
2. 引用官方文档、最佳实践作为依据
3. 寻求第三方意见，如技术负责人
4. 记录决策过程，形成团队共识

### Q3: 紧急修复如何快速上线？

**A**:
1. 使用Hotfix流程，跳过常规审查
2. 事后补审查，确保持续改进
3. 建立快速响应团队，7x24小时待命
4. 完善监控告警，提前发现问题

### Q4: 如何培养团队的PR文化？

**A**:
1. 建立明确的规范和流程
2. 定期培训，分享最佳实践
3. 使用工具降低执行成本
4. 表彰优秀实践，树立榜样
5. 持续改进，定期回顾优化

---

## 10. 变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-04-07 | 初始版本 | devops-architect |

---

**文档维护**: devops-architect  
**审核人**: backend-architect, frontend-architect  
**下次审查日期**: 2026-07-07
