# Research: 微型内存文件系统（迭代一）

**Created**: 2026-05-24

## 1. 编程语言：Java 17

**Decision**: Java 17（OpenJDK LTS）

**Rationale**:
- requirement.md 迭代二前瞻中明确以 Java 示例给出代码组织建议（interface Node、abstract class Node、PathUtil、SizeContext 等），暗示课程期望使用 Java
- Java 17 是当前最广泛使用的 LTS 版本，Gradescope OJ 环境支持良好
- 面向对象特性（interface、继承、多态）天然适合文件系统节点建模

**Alternatives considered**:
- C++：性能更好但 OO 抽象不如 Java 简洁，且迭代二前瞻的代码示例均为 Java 语法
- Python：开发快但题面建议的 interface/abstract class 模式在 Python 中不自然（ABC 模块笨重）
- Go：无继承，interface 嵌入方式与题面建议的 class hierarchy 模式差异较大

## 2. 构建工具：无（纯 javac）

**Decision**: 不使用 Maven/Gradle，直接使用 `javac` + `java` 或 IDE 构建

**Rationale**:
- OJ 提交通常要求单个/少量源文件，不需要构建脚本
- 项目无第三方依赖，JDK 标准库即够用
- 减少配置文件，降低 OJ 环境兼容风险

**Alternatives considered**:
- Maven：标准但 OJ 环境不一定支持，且项目简单时引入过度
- Gradle：同上

## 3. 节点建模：interface + 实现类

**Decision**: `Node` interface → `File` implements Node, `Directory` implements Node

**Rationale**:
- 遵循 requirement.md 迭代二前瞻建议："用 interface Node 或 abstract class Node 统一文件/目录/链接三类节点"
- interface 方式比 abstract class 更灵活，为未来 Link（可能不共享 File/Directory 的任何实现代码）留空间
- Directory 内部使用 `Map<String, Node>`（TreeMap）管理子节点，天然支持字母序

**Alternatives considered**:
- abstract class Node：也可行，但 Link 节点可能与 File/Directory 共享较少实现
- 不用接口，用 instanceof + 分支：迭代二前瞻明确警告"避免在主流程里散落大量 instanceof + 分支"

## 4. 大小计算上下文：SizeContext

**Decision**: 从迭代一开始，INFO 的递归遍历就携带 `SizeContext` 参数

**Rationale**:
- 迭代二前瞻明确建议："从一开始就把 INFO 的递归遍历设计成可携带上下文，例如 SizeContext { Set<NodeId> visited; boolean followLinks; }"
- 迭代一中 visited 和 followLinks 暂不使用，但接口预留避免迭代二推翻重写
- SizeContext 在迭代一只是简单透传，不增加复杂度

## 5. 路径解析：独立 PathUtil 类

**Decision**: 实现 `PathUtil` 工具类，包含 `validate(String absPath) → boolean` 和 `split(String absPath) → List<String>`

**Rationale**:
- 迭代二前瞻建议："路径解析与规范化独立成类，把切分/处理 . .. /去除多余 / 封装起来"
- 迭代一只实现 validate（合法性判定），为迭代二的 normalize（规范化）预留接口
- validate 规则：拒绝 `//`、尾 `/`（非单独 `/`）、段为 `.` 或 `..`

## 6. 测试策略：JUnit 5 + Golden Master

**Decision**: JUnit 5 参数化测试 + Golden Master Testing（已知输入 → 比对输出）

**Rationale**:
- JUnit 5 是 Java 生态标准测试框架，无需额外依赖（只需 junit-jupiter jar）
- `@ParameterizedTest` + `@CsvSource` 适合批量测试边界路径
- Golden master：将 requirement.md 中的示例直接编码为测试用例，确保输出逐字符一致

**Alternatives considered**:
- 纯手动测试：耗时且不可重复，不符合工程习惯
- TestNG：功能类似但 JUnit 5 更主流
