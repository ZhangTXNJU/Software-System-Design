package fs.service;

import fs.model.Directory;
import fs.model.File;
import fs.model.Node;
import fs.model.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class FileSystemTest {

    private FileSystem fs;

    @BeforeEach
    void setUp() {
        fs = new FileSystem();
    }

    // ---- resolve tests ----

    @Test
    void testResolveRoot() {
        Node root = fs.resolve("/");
        assertNotNull(root);
        assertEquals(NodeType.DIRECTORY, root.type());
    }

    @Test
    void testResolveNonExistent() {
        assertNull(fs.resolve("/nonexistent"));
    }

    @Test
    void testResolveAfterMkdir() {
        fs.mkdir("/usr");
        Node node = fs.resolve("/usr");
        assertNotNull(node);
        assertEquals(NodeType.DIRECTORY, node.type());
        assertEquals("usr", node.name());
    }

    // ---- MKDIR tests (US1) ----

    @Test
    void testMkdirCreateDirectory() {
        fs.mkdir("/usr");
        Node node = fs.resolve("/usr");
        assertNotNull(node);
        assertEquals(NodeType.DIRECTORY, node.type());
    }

    @Test
    void testMkdirNestedDirectories() {
        fs.mkdir("/usr");
        fs.mkdir("/usr/local");
        assertNotNull(fs.resolve("/usr/local"));
        assertEquals(NodeType.DIRECTORY, fs.resolve("/usr/local").type());
    }

    @Test
    void testMkdirParentNotExistSilent() {
        fs.mkdir("/a/b");
        assertNull(fs.resolve("/a"));
        assertNull(fs.resolve("/a/b"));
    }

    @Test
    void testMkdirExistingDirectoryNoOp() {
        fs.mkdir("/usr");
        fs.mkdir("/usr");
        assertNotNull(fs.resolve("/usr"));
        assertEquals(NodeType.DIRECTORY, fs.resolve("/usr").type());
    }

    @Test
    void testMkdirOverwritesFile() {
        fs.touch("/a", 10);
        assertEquals(NodeType.FILE, fs.resolve("/a").type());
        fs.mkdir("/a");
        assertEquals(NodeType.DIRECTORY, fs.resolve("/a").type());
        assertEquals(0, (long) fs.info("/a"));
    }

    // ---- TOUCH tests (US2) ----

    @Test
    void testTouchCreateFile() {
        fs.touch("/readme.md", 50);
        Node node = fs.resolve("/readme.md");
        assertNotNull(node);
        assertEquals(NodeType.FILE, node.type());
        assertEquals(50, (long) fs.info("/readme.md"));
    }

    @Test
    void testTouchOverwriteFile() {
        fs.touch("/readme.md", 50);
        fs.touch("/readme.md", 200);
        assertEquals(200, (long) fs.info("/readme.md"));
    }

    @Test
    void testTouchOverwriteDirectory() {
        fs.mkdir("/a");
        assertEquals(NodeType.DIRECTORY, fs.resolve("/a").type());
        fs.touch("/a", 50);
        assertEquals(NodeType.FILE, fs.resolve("/a").type());
        assertEquals(50, (long) fs.info("/a"));
    }

    @Test
    void testTouchParentNotExistSilent() {
        fs.touch("/a/f.txt", 10);
        assertNull(fs.resolve("/a"));
        assertNull(fs.resolve("/a/f.txt"));
    }

    @Test
    void testTouchNestedFile() {
        fs.mkdir("/usr");
        fs.mkdir("/usr/local");
        fs.touch("/usr/local/test.txt", 100);
        assertEquals(100, (long) fs.info("/usr/local/test.txt"));
    }

    // ---- LS tests (US3) ----

    @Test
    void testLsRootAlphabetical() {
        fs.mkdir("/usr");
        fs.touch("/readme.md", 50);
        List<String> result = fs.ls("/");
        assertEquals(2, result.size());
        assertEquals("readme.md", result.get(0));
        assertEquals("usr", result.get(1));
    }

    @Test
    void testLsOnFile() {
        fs.touch("/readme.md", 50);
        List<String> result = fs.ls("/readme.md");
        assertEquals(1, result.size());
        assertEquals("readme.md", result.get(0));
    }

    @Test
    void testLsEmptyDirectory() {
        fs.mkdir("/empty");
        List<String> result = fs.ls("/empty");
        assertTrue(result.isEmpty());
    }

    // ---- INFO tests (US4) ----

    @Test
    void testInfoFile() {
        fs.touch("/readme.md", 50);
        assertEquals(50, (long) fs.info("/readme.md"));
    }

    @Test
    void testInfoEmptyDirectory() {
        fs.mkdir("/empty");
        assertEquals(0, (long) fs.info("/empty"));
    }

    @Test
    void testInfoRecursiveSum() {
        fs.mkdir("/usr");
        fs.mkdir("/usr/local");
        fs.touch("/usr/local/test.txt", 100);
        fs.touch("/readme.md", 50);
        assertEquals(150, (long) fs.info("/"));
        assertEquals(100, (long) fs.info("/usr"));
    }

    // ---- Invalid path tests (Edge Cases) ----

    @Test
    void testMkdirInvalidPathSilent() {
        fs.mkdir("//a");
        assertNull(fs.resolve("//a"));
    }

    @Test
    void testTouchInvalidPathSilent() {
        fs.touch("/a/", 10);
        assertNull(fs.resolve("/a/"));
    }

    // ---- Intermediate path is file ----

    @Test
    void testMkdirWhenIntermediateIsFile() {
        fs.touch("/a", 10);
        fs.mkdir("/a/b");
        // /a is a file, so parent for /a/b is not a directory → silent ignore
        assertNull(fs.resolve("/a/b"));
    }

    @Test
    void testTouchWhenIntermediateIsFile() {
        fs.touch("/a", 10);
        fs.touch("/a/b", 20);
        // /a is a file, so parent for /a/b is not a directory → silent ignore
        assertNull(fs.resolve("/a/b"));
    }
}
