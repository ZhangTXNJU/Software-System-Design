# Implementation Plan: 微型内存文件系统（迭代一）

**Branch**: `001-micro-memory-filesystem` | **Date**: 2026-05-24 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-micro-memory-filesystem/spec.md`

## Summary

实现一个命令行「微型内存文件系统」，从标准输入读取 MKDIR / TOUCH / LS / INFO 四条指令，在内存中维护一棵以 `/` 为根的文件树，并将结果输出到标准输出。技术方案：Java 17 + 面向对象设计（接口/抽象类分层），无外部依赖，通过 Gradescope OJ 自动评测。

## Technical Context

**Language/Version**: Java 17（LTS，满足 OJ 环境兼容性；迭代二前瞻建议使用 interface/abstract class）

**Primary Dependencies**: 无第三方依赖。仅使用 JDK 标准库（java.util.* 集合类、java.io.* 标准 I/O）

**Storage**: 纯内存（无持久化），节点以对象引用维护树形关系

**Testing**: JUnit 5（单元测试 + 参数化集成测试），Golden master testing（已知输入 → 比对预期输出）

**Target Platform**: JDK 17+，跨平台（Linux/macOS/Windows），最终运行于 Gradescope OJ 环境

**Project Type**: CLI（命令行应用），stdin → 处理 → stdout，OJ 评测格式

**Performance Goals**: 单次运行处理数百条指令，每条指令 O(depth) 或 O(children) 即可

**Constraints**: 输出必须与 OJ 预期逐字符一致；路径合法性严格按迭代一规则判定；无路径规范化

**Scale/Scope**: 约 200-400 行代码，4 条指令，3-4 个核心类，纯逻辑无 IO/网络

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| Constitution template (not customized) | N/A | 项目尚未定义宪法原则，跳过 gate 检查 |

**Verdict**: PASS (no constitution violations to address)

## Project Structure

### Documentation (this feature)

```text
specs/001-micro-memory-filesystem/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (CLI command spec)
└── tasks.md             # Phase 2 output (/speckit-tasks)
```

### Source Code (repository root)

```text
src/
├── main/java/fs/
│   ├── Main.java            # 入口：读取 stdin，分发指令，输出结果
│   ├── model/
│   │   ├── Node.java        # 统一节点抽象（interface）
│   │   ├── File.java        # 文件实现
│   │   └── Directory.java   # 目录实现
│   ├── service/
│   │   ├── FileSystem.java  # 文件系统核心（树管理、路径解析）
│   │   └── CommandParser.java # 指令解析与分发
│   └── context/
│       └── SizeContext.java  # 大小计算上下文（为迭代二链接/防环预留）

tests/
└── java/fs/
    ├── model/
    │   ├── FileTest.java
    │   └── DirectoryTest.java
    ├── service/
    │   ├── FileSystemTest.java
    │   └── CommandParserTest.java
    └── integration/
        └── OJIntegrationTest.java  # OJ 格式端到端测试
```

**Structure Decision**: 采用单项目结构，`model/` 层封装节点抽象（为迭代二的 Link 节点预留扩展点），`service/` 层负责指令解析与文件系统操作。测试按 unit / integration 分层。

## Complexity Tracking

无违规需要记录（constitution 为模板占位符，无自定义原则）。

---

## 复杂度分析

| 维度 | 评估 |
|------|------|
| **整体难度** | 初级→中级。核心逻辑是树操作 + 路径解析，数据结构课标准难度 |
| **指令复杂度** | MKDIR / TOUCH / LS 为 O(depth) 操作；INFO 为 O(n) 递归遍历 |
| **边界条件密度** | 较高。非法路径判定（3 种模式）、文件↔目录覆盖（4 种组合）、父目录不存在、空目录 LS |
| **迭代二扩展点** | 需预留 Node 接口、SizeContext、PathUtil 规范化入口 |
| **代码量估算** | ~250-400 行 Java（含测试 ~600-800 行） |

## 预估时间

| 阶段 | 时长 | 说明 |
|------|------|------|
| 数据模型实现（model/） | 1-2 小时 | Node 接口 + File + Directory + SizeContext |
| 指令解析与分发（service/） | 1-2 小时 | CommandParser + FileSystem 核心逻辑 |
| 路径解析与校验 | 1 小时 | 路径切分、合法性判定（// . .. 尾斜杠） |
| 集成与调试 | 1-2 小时 | 端到端 OJ 格式测试、边界条件打磨 |
| 单元测试编写 | 1-2 小时 | 覆盖所有指令 + 边界条件 + 参数化测试 |
| **总计** | **5-9 小时**（约 1 个工作日） | 取决于对 Java 和 OJ 评测的熟悉程度 |

## 技术方案

### 架构：三层分离

```
stdin → CommandParser → FileSystem → Model (Node/File/Directory)
                ↓
            stdout
```

- **CommandParser**: 按空格拆分行 → 识别命令动词 → 提取参数 → 调用 FileSystem 方法 → 收集输出
- **FileSystem**: 持有根目录引用，提供 `resolve(path)` 返回目标节点（或 null），提供 `mkdir/touch/ls/info` 方法
- **Model**: `Node` 接口统一 File / Directory 的 `type()`、`name()`、`size(SizeContext)`。Directory 内部用 `Map<String, Node>` 管理子节点

### 路径解析（迭代一规则）

```
路径校验：
1. 不能包含 "//"
2. 不能以 "/" 结尾（单独 "/" 除外）
3. 每个路径段不能是 "." 或 ".."

路径解析：
1. 从根 "/" 开始
2. 按 "/" 切分段（跳过首段空串）
3. 逐段在 Directory.getChild() 中查找
4. 中间节点不存在 → 返回 null（父目录不存在）
5. 中间节点是 File → 返回 null（File 无子节点）
```

### 覆盖语义

```
MKDIR /path:
  parent = resolve(parentOf(path))
  if parent == null → 忽略
  if parent.getChild(name) is File → 替换为 Directory
  if parent.getChild(name) is Directory → 保持（no-op）
  else → parent.putChild(new Directory(name))

TOUCH /path size:
  parent = resolve(parentOf(path))
  if parent == null → 忽略
  parent.putChild(new File(name, size))  // 无条件 put 覆盖
```

### 迭代二预留扩展点

1. **Node 接口**: `NodeType type()` 返回 FILE / DIRECTORY / LINK（迭代一只用前两种）
2. **SizeContext**: 携带 `Set<Node> visited` + `boolean followLinks`，迭代一的 INFO 传 `followLinks=false`
3. **PathUtil 类**: 迭代一实现 `validate()` 做合法性校验；迭代二新增 `normalize()` 做规范化，互不干扰

## 实现策略

### 推荐顺序（TDD 增量构建）

1. **Model 层先写**：File + Directory → Node 接口 → SizeContext
2. **FileSystem 核心**：resolve(path) → mkdir → touch → 先用简单 case 手动验证
3. **CommandParser**：解析 stdin → 分发到 FileSystem → 收集输出
4. **边界条件逐个覆盖**：非法路径判定 → 覆盖语义 → 父目录不存在
5. **集成测试**：抄录 requirement.md 中的示例作为 golden test

### 关键风险与对策

| 风险 | 对策 |
|------|------|
| OJ 输出格式不匹配（多余空行/空格） | 严格控制 println 位置，golden test 逐字符比对 |
| 覆盖语义实现与题面不一致 | 对照 requirement.md 第 2 节（两个差异说明）逐条测试 |
| 路径判定遗漏边界模式 | 写参数化测试覆盖：`//a`、`/a/`、`/./a`、`/a/../b`、`/a//b` |
| TOUCH 覆盖目录后原子树悬挂 | FileSystem 的 putChild 负责替换引用，旧节点由 GC 回收 |

## 测试策略

### 分层测试

```
Integration (OJ端到端)  ─  覆盖 requirement.md 中所有示例 + 自制用例
     │
Unit (FileSystem)       ─  每个指令方法独立测试：正常路径 + 边界 + 覆盖语义
     │
Unit (Model)            ─  File/Directory size 计算、Directory getChild/putChild
     │
Unit (CommandParser)    ─  指令解析正确性、非法指令处理
```

### 评分标准（OJ 评测等价）

| 评分维度 | 权重 | 说明 |
|----------|------|------|
| 基础指令正确性 | 40% | MKDIR / TOUCH / LS / INFO 的基本功能 |
| 边界条件处理 | 30% | 非法路径、父目录不存在、覆盖语义、空目录 LS |
| 路径规则严格性 | 20% | 不做路径规范化、按迭代一规则判定合法性 |
| 输出格式精确性 | 10% | 无多余空行/空格、字母序正确、每行一条 |

OJ 评测通过 = 全部隐藏测试用例输出与预期一致，满分 100 分。

### 自测检查清单

- [ ] requirement.md 中 ALL 示例（LS 示例 1/2、INFO 示例 1/2、OJ 综合示例）输出完全一致
- [ ] `MKDIR /a/b` 父目录不存在 → 无输出
- [ ] `TOUCH /a/f.txt 10` 父目录不存在 → 无输出
- [ ] `//a`、`/a/`、`/./a`、`/a/../b`、`/a//b` → 全部忽略无输出
- [ ] TOUCH 覆盖目录 → 文件替换目录，INFO 反映新大小
- [ ] MKDIR 覆盖文件 → 目录替换文件
- [ ] MKDIR 已存在目录 → no-op
- [ ] LS 文件 → 输出文件名
- [ ] LS 空目录 → 无输出
- [ ] INFO 递归目录大小 → 多层嵌套计算正确
- [ ] LS / INFO 按字母序输出
