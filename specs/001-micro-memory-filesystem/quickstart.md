# Quickstart: 微型内存文件系统（迭代一）

## 环境要求

- JDK 17+
- 任意文本编辑器或 IDE（推荐 IntelliJ IDEA 或 VS Code）

## 快速编译运行

```bash
# 编译
javac -d out src/main/java/fs/**/*.java

# 运行（交互模式）
java -cp out fs.Main

# 运行（文件输入）
java -cp out fs.Main < test_input.txt

# 运行（管道输入）
echo -e "MKDIR /usr\nTOUCH /readme.md 50\nLS /\nINFO /" | java -cp out fs.Main
```

## 预期输出示例

输入：
```
MKDIR /usr
MKDIR /usr/local
TOUCH /usr/local/test.txt 100
TOUCH /readme.md 50
LS /
INFO /
INFO /usr
```

输出：
```
readme.md
usr
150
100
```

## 运行测试

```bash
# 编译测试（需要 JUnit 5 jar）
javac -d out -cp out:junit-platform-console-standalone.jar \
  src/main/java/fs/**/*.java tests/java/fs/**/*.java

# 运行测试
java -jar junit-platform-console-standalone.jar \
  --class-path out --scan-class-path
```

## 项目入口

- `src/main/java/fs/Main.java` — 程序主入口，读取 stdin 并循环处理指令
- `src/main/java/fs/model/Node.java` — 节点统一接口
- `src/main/java/fs/service/FileSystem.java` — 文件系统核心逻辑
- `src/main/java/fs/service/CommandParser.java` — 指令解析与分发
