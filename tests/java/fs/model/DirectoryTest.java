package fs.model;

import fs.context.SizeContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;

class DirectoryTest {

    @Test
    void testCreateDirectory() {
        Directory dir = new Directory("usr");
        assertEquals("usr", dir.name());
        assertEquals(NodeType.DIRECTORY, dir.type());
        assertEquals(0, dir.size(SizeContext.empty()));
        assertTrue(dir.listChildren().isEmpty());
    }

    @Test
    void testPutAndGetChild() {
        Directory dir = new Directory("usr");
        File file = new File("test.txt", 100);
        dir.putChild("test.txt", file);
        assertEquals(file, dir.getChild("test.txt"));
        assertEquals(100, dir.size(SizeContext.empty()));
    }

    @Test
    void testGetChildNonExistent() {
        Directory dir = new Directory("usr");
        assertNull(dir.getChild("nonexistent"));
    }

    @Test
    void testListChildrenAlphabetical() {
        Directory dir = new Directory("root");
        dir.putChild("banana", new File("banana", 1));
        dir.putChild("apple", new File("apple", 1));
        dir.putChild("cherry", new File("cherry", 1));

        Collection<Node> children = dir.listChildren();
        String[] expected = {"apple", "banana", "cherry"};
        int i = 0;
        for (Node child : children) {
            assertEquals(expected[i++], child.name());
        }
    }

    @Test
    void testRemoveChild() {
        Directory dir = new Directory("usr");
        dir.putChild("test.txt", new File("test.txt", 100));
        dir.removeChild("test.txt");
        assertNull(dir.getChild("test.txt"));
        assertEquals(0, dir.size(SizeContext.empty()));
    }

    @Test
    void testNestedDirectoriesSize() {
        Directory root = new Directory("/");
        Directory usr = new Directory("usr");
        Directory local = new Directory("local");
        local.putChild("test.txt", new File("test.txt", 100));
        usr.putChild("local", local);
        root.putChild("usr", usr);
        root.putChild("readme.md", new File("readme.md", 50));

        assertEquals(150, root.size(SizeContext.empty()));
        assertEquals(100, usr.size(SizeContext.empty()));
        assertEquals(100, local.size(SizeContext.empty()));
    }

    @Test
    void testPutChildOverwrites() {
        Directory dir = new Directory("usr");
        dir.putChild("a", new File("a", 10));
        assertEquals(10, dir.size(SizeContext.empty()));
        dir.putChild("a", new File("a", 20));
        assertEquals(20, dir.size(SizeContext.empty()));
    }
}
