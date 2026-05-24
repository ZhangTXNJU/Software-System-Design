# Data Model: 微型内存文件系统（迭代一）

**Created**: 2026-05-24

## Entity Relationship

```
Node (interface)
├── name(): String
├── type(): NodeType   # FILE | DIRECTORY (迭代二: + LINK)
├── size(SizeContext): long
│
├── File implements Node
│   ├── -name: String
│   └── -contentSize: long
│
└── Directory implements Node
    ├── -name: String
    ├── -children: Map<String, Node>  # TreeMap → 字母序
    ├── getChild(String): Node | null
    ├── putChild(String, Node): void
    ├── removeChild(String): void
    └── listChildren(): Collection<Node>

SizeContext (预留)
├── -visited: Set<Node>          # 迭代二防环用
├── -followLinks: boolean        # 迭代二链接用
└── +empty(): SizeContext        # 迭代一用空上下文

FileSystem
├── -root: Directory
├── resolve(String absPath): Node | null
├── mkdir(String absPath): void
├── touch(String absPath, long size): void
├── ls(String absPath): List<String> | null
└── info(String absPath): long | null

CommandParser
├── +parse(String line): Command
└── +execute(Command, FileSystem): String | null
```

## Validation Rules

### Path Validation (validate before resolve)

| Rule | Example (rejected) | Example (accepted) |
|------|---------------------|---------------------|
| 包含 `//` | `/a//b` | `/a/b` |
| 以 `/` 结尾且不是单独的 `/` | `/a/`, `/a/b/` | `/`, `/a` |
| 路径段为 `.` | `/./a`, `/a/.` | `/a/b` |
| 路径段为 `..` | `/a/../b`, `/..` | `/a/b` |

### Command Validation

| Command | Pre-condition | On Failure |
|---------|--------------|------------|
| MKDIR | parent directory exists (and is directory) | Silent ignore |
| TOUCH | parent directory exists (and is directory) | Silent ignore |
| LS | path exists (guaranteed by OJ) | N/A |
| INFO | path exists (guaranteed by OJ) | N/A |

## State Transitions

### MKDIR /path/name
```
State before              → State after
────────────────────────────────────────────
no node at /path/name     → new Directory at /path/name
File at /path/name        → replaced by Directory at /path/name
Directory at /path/name   → unchanged (no-op)
parent /path not exist    → unchanged (silent ignore)
```

### TOUCH /path/name size
```
State before              → State after
────────────────────────────────────────────
no node at /path/name     → new File at /path/name with contentSize=size
File at /path/name        → File at /path/name with contentSize=size (overwrite)
Directory at /path/name   → replaced by File at /path/name with contentSize=size
parent /path not exist    → unchanged (silent ignore)
```

## Size Calculation

```
Node.size(ctx):
  File → return contentSize
  Directory → sum(child.size(ctx) for child in children.values())
  (迭代二: Link → if ctx.followLinks and not ctx.visited: 
                mark visited, delegate to target.size(ctx))
```
