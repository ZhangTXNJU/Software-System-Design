# Tasks: 微型内存文件系统（迭代一）

**Input**: Design documents from `/specs/001-micro-memory-filesystem/`

**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/

**Tests**: Included per plan.md testing strategy (JUnit 5, Golden Master).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Source**: `src/main/java/fs/` at repository root
- **Tests**: `tests/java/fs/` at repository root
- Java 17, no build tool (javac + java)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create project directory structure

- [x] T001 Create project directory structure: `src/main/java/fs/model/`, `src/main/java/fs/service/`, `src/main/java/fs/context/`, `tests/java/fs/model/`, `tests/java/fs/service/`, `tests/java/fs/integration/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core model classes and utilities that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 [P] Implement NodeType enum (FILE, DIRECTORY; 迭代二+LINK) in `src/main/java/fs/model/NodeType.java`
- [x] T003 [P] Implement Node interface (name(), type(), size(SizeContext)) in `src/main/java/fs/model/Node.java`
- [x] T004 [P] Implement SizeContext class (visited set, followLinks flag, empty() factory) in `src/main/java/fs/context/SizeContext.java`
- [x] T005 [P] Implement PathUtil class with validate(absPath) and split(absPath) methods in `src/main/java/fs/service/PathUtil.java`
- [x] T006 [P] Implement File class (implements Node; name, contentSize, size returns contentSize) in `src/main/java/fs/model/File.java`
- [x] T007 [P] Implement Directory class (implements Node; name, TreeMap children, getChild/putChild/listChildren, size sums children) in `src/main/java/fs/model/Directory.java`
- [x] T008 Implement FileSystem skeleton with root Directory and resolve(absPath) method in `src/main/java/fs/service/FileSystem.java`

**Checkpoint**: Foundation ready — user story implementation can now begin

---

## Phase 3: User Story 1 - 创建目录结构 MKDIR (Priority: P1) 🎯 MVP

**Goal**: 用户通过 MKDIR 在内存中创建目录节点，支持父目录不存在时静默忽略、文件→目录覆盖、已存在目录 no-op

**Independent Test**: 执行 MKDIR 序列后用 LS/INFO 验证目录树正确性

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T009 [P] [US1] Unit test for Directory model (putChild, getChild, listChildren ordering) in `tests/java/fs/model/DirectoryTest.java`
- [x] T010 [P] [US1] Unit test for MKDIR scenarios (create dir, parent not exist → silent, File→Directory replacement, existing Directory→no-op) in `tests/java/fs/service/FileSystemTest.java` (partial: mkdir tests only)

### Implementation for User Story 1

- [x] T011 [US1] Implement FileSystem.mkdir(absPath) in `src/main/java/fs/service/FileSystem.java` (depends on T008 resolve, T007 Directory)
- [x] T012 [US1] Verify MKDIR tests (T009, T010) pass

**Checkpoint**: MKDIR fully functional and independently testable

---

## Phase 4: User Story 2 - 创建和覆盖文件 TOUCH (Priority: P1)

**Goal**: 用户通过 TOUCH 创建文件设定大小，支持覆盖同名文件（更新大小）、覆盖目录（文件替换目录）、父目录不存在静默忽略

**Independent Test**: 执行 TOUCH 创建/覆盖后用 INFO 验证大小正确

### Tests for User Story 2 ⚠️

- [x] T013 [P] [US2] Unit test for File model (name, contentSize, size()) in `tests/java/fs/model/FileTest.java`
- [x] T014 [P] [US2] Unit test for TOUCH scenarios (create file, overwrite File→size changed, overwrite Directory→File, parent not exist→silent) in `tests/java/fs/service/FileSystemTest.java` (add: touch tests)

### Implementation for User Story 2

- [x] T015 [US2] Implement FileSystem.touch(absPath, size) in `src/main/java/fs/service/FileSystem.java`
- [x] T016 [US2] Verify TOUCH tests (T013, T014) pass

**Checkpoint**: MKDIR + TOUCH both functional and independently testable

---

## Phase 5: User Story 3 - 浏览目录内容 LS (Priority: P2)

**Goal**: 用户通过 LS 列出目录直接子节点（字母序），对文件仅输出文件名

**Independent Test**: 构造已知目录树，执行 LS 比对输出内容与顺序

### Tests for User Story 3 ⚠️

- [x] T017 [P] [US3] Unit test for LS scenarios (list directory children alphabetically, LS on file→file name, empty directory→no output) in `tests/java/fs/service/FileSystemTest.java` (add: ls tests)

### Implementation for User Story 3

- [x] T018 [US3] Implement FileSystem.ls(absPath) returning sorted child names list in `src/main/java/fs/service/FileSystem.java`
- [x] T019 [US3] Verify LS tests (T017) pass

**Checkpoint**: MKDIR + TOUCH + LS all functional

---

## Phase 6: User Story 4 - 查询节点大小 INFO (Priority: P2)

**Goal**: 用户通过 INFO 查询节点大小：文件=自身大小，目录=递归子树文件大小总和

**Independent Test**: 构造已知大小的多层目录树，对文件和各级目录执行 INFO 比对数值

### Tests for User Story 4 ⚠️

- [x] T020 [P] [US4] Unit test for INFO scenarios (file size, empty directory→0, nested directory recursive sum) in `tests/java/fs/service/FileSystemTest.java` (add: info tests)

### Implementation for User Story 4

- [x] T021 [US4] Implement FileSystem.info(absPath) returning recursive size in `src/main/java/fs/service/FileSystem.java`
- [x] T022 [US4] Verify INFO tests (T020) pass

**Checkpoint**: All four commands (MKDIR, TOUCH, LS, INFO) individually functional

---

## Phase 7: Integration & CLI

**Purpose**: Wire commands together, stdin/stdout I/O, end-to-end golden master tests

- [x] T023 Implement CommandParser parsing stdin lines into method calls in `src/main/java/fs/service/CommandParser.java`
- [x] T024 Implement Main.java entry point (read stdin lines, dispatch via CommandParser, write output to stdout) in `src/main/java/fs/Main.java`
- [x] T025 [P] Integration test: requirement.md ALL examples (LS示例1/2, INFO示例1/2, OJ综合示例) as golden master in `tests/java/fs/integration/OJIntegrationTest.java`
- [x] T026 Verify all golden master tests pass with exact output match

**Checkpoint**: Full CLI application working end-to-end

---

## Phase 8: Polish & Edge Cases

**Purpose**: Hardening against all boundary conditions from spec.md Edge Cases

- [x] T027 [P] Path validation edge-case parameterized test: `//a`, `/a/`, `/./a`, `/a/../b`, `/a//b`, `/..`, `/a/b/.` in `tests/java/fs/service/PathUtilTest.java`
- [x] T028 Implement PathUtil.validate() enforcement in FileSystem (reject invalid paths before resolve; return null → silent ignore)
- [x] T029 [P] Edge case test: TOUCH 覆盖深层目录后 INFO 验证原子树被正确替换 in `tests/java/fs/integration/OJIntegrationTest.java`
- [x] T030 [P] Edge case test: MKDIR 覆盖文件后 INFO 验证文件被目录替换 in `tests/java/fs/integration/OJIntegrationTest.java`
- [x] T031 Verify all edge case tests from plan.md 自测检查清单 pass
- [x] T032 Run quickstart.md validation: compile + run with sample input, verify output matches expected

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Setup (T001) — BLOCKS all user stories
- **US1 MKDIR (Phase 3)**: Depends on Foundational — No dependencies on other stories
- **US2 TOUCH (Phase 4)**: Depends on Foundational — Independent of US1 (touches different FileSystem methods)
- **US3 LS (Phase 5)**: Depends on Foundational — Independent of US1/US2
- **US4 INFO (Phase 6)**: Depends on Foundational — Independent of US1/US2/US3
- **Integration (Phase 7)**: Depends on US1+US2+US3+US4 (all commands implemented)
- **Polish (Phase 8)**: Depends on Integration

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational — No dependencies on other stories
- **US2 (P1)**: Can start after Foundational — No dependencies on US1 (different FileSystem method)
- **US3 (P2)**: Can start after Foundational — No dependencies on US1/US2
- **US4 (P2)**: Can start after Foundational — No dependencies on US1/US2/US3

### Within Each User Story

- Tests written FIRST, must FAIL before implementation
- Implementation follows
- Verify tests pass before moving to next story
- Story checkpoint validated before moving to next priority

### Parallel Opportunities

- T002–T007 (all Foundational model classes) can run in parallel — different files
- T009+T010 (US1 tests) can run in parallel
- T013+T014 (US2 tests) can run in parallel
- US3 and US4 can run in parallel after their respective upstream stories
- T025, T027, T029, T030 (integration/edge tests) can run in parallel
- **All four user stories (US1–US4)** can be implemented in parallel after Foundational if multiple developers

---

## Parallel Example: Phase 2 Foundational

```bash
# Launch all model class tasks together (different files, no cross-dependencies):
Task: "T002 Implement NodeType enum in src/main/java/fs/model/NodeType.java"
Task: "T003 Implement Node interface in src/main/java/fs/model/Node.java"
Task: "T004 Implement SizeContext class in src/main/java/fs/context/SizeContext.java"
Task: "T005 Implement PathUtil class in src/main/java/fs/service/PathUtil.java"
Task: "T006 Implement File class in src/main/java/fs/model/File.java"
Task: "T007 Implement Directory class in src/main/java/fs/model/Directory.java"

# After T002–T007 complete:
Task: "T008 Implement FileSystem skeleton in src/main/java/fs/service/FileSystem.java"
```

## Parallel Example: User Story 1 (MKDIR)

```bash
# Launch tests first (TDD):
Task: "T009 [P] [US1] DirectoryTest.java"
Task: "T010 [P] [US1] FileSystemTest.java (mkdir tests)"

# After tests written and FAILING:
Task: "T011 [US1] Implement FileSystem.mkdir()"
```

---

## Implementation Strategy

### MVP First (User Story 1 + 2: MKDIR + TOUCH)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: US1 MKDIR
4. Complete Phase 4: US2 TOUCH
5. **STOP and VALIDATE**: Test MKDIR + TOUCH together — can create trees
6. This is a viable MVP: you can build a file tree and verify with INFO

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. US1 MKDIR + US2 TOUCH → Can create file trees (MVP!)
3. US3 LS → Can browse trees
4. US4 INFO → Can query sizes
5. Integration → Full CLI app
6. Polish → OJ-ready

### Quick Path (Single Developer, Sequential)

```
T001 → T002–T007(parallel) → T008 → T009–T010(parallel) → T011–T012 →
T013–T014(parallel) → T015–T016 → T017 → T018–T019 → T020 → T021–T022 →
T023–T024 → T025–T026 → T027–T032
```

---

## Notes

- [P] tasks = different files, no dependencies — run in parallel
- [Story] label maps task to specific user story for traceability
- Each user story is independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All tests are JUnit 5 (no external deps beyond junit-jupiter jar)
- Golden master tests must match OJ expected output character-by-character
- Java 17, no build tool — compile with `javac -d out` and run with `java -cp out`
