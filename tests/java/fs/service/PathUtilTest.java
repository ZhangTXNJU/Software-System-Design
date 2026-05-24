package fs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class PathUtilTest {

    @Test
    void testValidateRoot() {
        assertTrue(PathUtil.validate("/"));
    }

    @Test
    void testValidateNormalPath() {
        assertTrue(PathUtil.validate("/usr/local/test.txt"));
    }

    @Test
    void testValidateSingleSegment() {
        assertTrue(PathUtil.validate("/readme.md"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/a//b",
        "/a///b",
        "//a",
        "/a/",
        "/a/b/",
        "/./a",
        "/a/./b",
        "/a/.",
        "/a/../b",
        "/a/..",
        "/..",
        "/../a",
        "",
        "relative/path",
        "a"
    })
    void testValidateInvalidPaths(String path) {
        assertFalse(PathUtil.validate(path), "Path should be invalid: " + path);
    }

    @Test
    void testSplitRoot() {
        List<String> parts = PathUtil.split("/");
        assertTrue(parts.isEmpty());
    }

    @Test
    void testSplitSingleSegment() {
        List<String> parts = PathUtil.split("/usr");
        assertEquals(1, parts.size());
        assertEquals("usr", parts.get(0));
    }

    @Test
    void testSplitMultiSegment() {
        List<String> parts = PathUtil.split("/usr/local/test.txt");
        assertEquals(3, parts.size());
        assertEquals("usr", parts.get(0));
        assertEquals("local", parts.get(1));
        assertEquals("test.txt", parts.get(2));
    }
}
