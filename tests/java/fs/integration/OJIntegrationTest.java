package fs.integration;

import fs.service.FileSystem;
import fs.service.CommandParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OJIntegrationTest {

    private FileSystem fs;
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        fs = new FileSystem();
        parser = new CommandParser(fs);
    }

    private void executeLines(String... lines) {
        for (String line : lines) {
            String output = parser.execute(line);
            if (output != null) {
                // output is validated by individual test assertions
            }
        }
    }

    // ---- OJ Sample from requirement.md ----

    @Test
    void testOjComprehensiveExample() {
        assertEquals("readme.md\nusr\n150\n100",
            combine(
                executeAndCollect("MKDIR /usr"),
                executeAndCollect("MKDIR /usr/local"),
                executeAndCollect("TOUCH /usr/local/test.txt 100"),
                executeAndCollect("TOUCH /readme.md 50"),
                executeAndCollect("LS /"),
                executeAndCollect("INFO /"),
                executeAndCollect("INFO /usr")
            ));
    }

    // ---- LS Example 1 from requirement.md ----

    @Test
    void testLsExample1() {
        assertEquals("readme.md\nusr",
            combine(
                executeAndCollect("MKDIR /usr"),
                executeAndCollect("TOUCH /readme.md 50"),
                executeAndCollect("LS /")
            ));
    }

    // ---- LS Example 2 from requirement.md ----

    @Test
    void testLsExample2() {
        assertEquals("readme.md",
            combine(
                executeAndCollect("TOUCH /readme.md 50"),
                executeAndCollect("LS /readme.md")
            ));
    }

    // ---- INFO Example 1 from requirement.md ----

    @Test
    void testInfoExample1() {
        assertEquals("50",
            combine(
                executeAndCollect("TOUCH /readme.md 50"),
                executeAndCollect("INFO /readme.md")
            ));
    }

    // ---- INFO Example 2 from requirement.md ----

    @Test
    void testInfoExample2() {
        assertEquals("150",
            combine(
                executeAndCollect("MKDIR /usr"),
                executeAndCollect("MKDIR /usr/local"),
                executeAndCollect("TOUCH /usr/local/test.txt 100"),
                executeAndCollect("TOUCH /readme.md 50"),
                executeAndCollect("INFO /")
            ));
    }

    // ---- Edge case: TOUCH overwrite directory, verify subtree replaced ----

    @Test
    void testTouchOverwriteDirectorySubtreeReplaced() {
        executeLines("MKDIR /dir", "MKDIR /dir/sub", "TOUCH /dir/sub/f.txt 100");
        assertEquals(100, (long) fs.info("/dir"));
        // Now overwrite /dir with a file
        executeLines("TOUCH /dir 50");
        assertEquals(50, (long) fs.info("/dir"));
    }

    // ---- Edge case: MKDIR overwrite file ----

    @Test
    void testMkdirOverwriteFile() {
        executeLines("TOUCH /a 10");
        assertEquals(10, (long) fs.info("/a"));
        executeLines("MKDIR /a");
        assertEquals(0, (long) fs.info("/a"));
    }

    // ---- Invalid paths full integration test ----

    @Test
    void testInvalidPathsAllSilent() {
        String[] commands = {
            "MKDIR //a", "MKDIR /a/", "MKDIR /./a", "MKDIR /a/../b",
            "TOUCH //a 10", "TOUCH /b/ 10", "TOUCH /./c 10", "TOUCH /d/../e 10"
        };
        for (String cmd : commands) {
            assertNull(parser.execute(cmd), "Command should be silent: " + cmd);
        }
        // file system should still be empty (only root)
        assertEquals(0, (long) fs.info("/"));
    }

    // ---- Helper ----

    private String executeAndCollect(String line) {
        return parser.execute(line);
    }

    private String combine(String... outputs) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : outputs) {
            if (s != null) {
                if (!first) sb.append("\n");
                sb.append(s);
                first = false;
            }
        }
        return sb.toString();
    }
}
