# CLI Command Contract: 微型内存文件系统（迭代一）

**Version**: 1.0 | **Created**: 2026-05-24

## Interface

- **Input**: Standard Input (stdin), 每行一条指令
- **Output**: Standard Output (stdout), 仅可输出指令产生输出
- **Exit Code**: 0 (正常结束，不依赖 exit code 传递结果)

## Command Grammar

```
<program>      ::= <line>*
<line>         ::= <command> "\n"
<command>      ::= <mkdir> | <touch> | <ls> | <info>
<mkdir>        ::= "MKDIR " <absPath>
<touch>        ::= "TOUCH " <absPath> " " <integer>
<ls>           ::= "LS " <absPath>
<info>         ::= "INFO " <absPath>
<absPath>      ::= "/" | "/" <segments>
<segments>     ::= <segment> | <segment> "/" <segments>
<segment>      ::= <char>+
<char>         ::= any printable ASCII char except "/"
<integer>      ::= non-negative integer (0..2^63-1)
```

## Command Semantics

### MKDIR

```
MKDIR <absPath>

Pre:  absPath must be valid (no //, no trailing / except root, no . or ..)
      parent directory of absPath must exist and be a Directory

Post: A Directory node is created at absPath, or an existing File is
      replaced by a Directory. If the path already holds a Directory,
      the operation is a no-op.

Fail: If pre-conditions are not met → no output, no state change.

Output: None (silent on success).
```

### TOUCH

```
TOUCH <absPath> <size>

Pre:  absPath must be valid.
      parent directory of absPath must exist and be a Directory.

Post: A File node with contentSize=<size> is placed at absPath, 
      overwriting any existing node (File or Directory) at that path.

Fail: If pre-conditions are not met → no output, no state change.

Output: None (silent on success).
```

### LS

```
LS <absPath>

Pre:  absPath must be valid.
      absPath must resolve to an existing Node (guaranteed by OJ).

Post: No state change.

Output: If absPath resolves to a Directory → each direct child's name
        on a separate line, sorted alphabetically (natural String order).
        If absPath resolves to a File → the file's name on one line.
        If Directory is empty → no output.
```

### INFO

```
INFO <absPath>

Pre:  absPath must be valid.
      absPath must resolve to an existing Node (guaranteed by OJ).

Post: No state change.

Output: A single line containing the node's size as a base-10 integer.
        For a File → its contentSize.
        For a Directory → sum of contentSize of all descendant Files.
```

## Path Validity Rules (Iteration 1)

| Pattern | Valid? | Example Rejected |
|---------|--------|-------------------|
| `//` anywhere | No | `/a//b` |
| Trailing `/` (not root) | No | `/a/`, `/a/b/` |
| Segment is `.` | No | `/./a`, `/a/.` |
| Segment is `..` | No | `/a/../b`, `/..` |
| Root `/` | Yes | `/` |
| Normal absolute path | Yes | `/a/b/c` |
