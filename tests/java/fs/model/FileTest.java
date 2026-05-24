package fs.model;

import fs.context.SizeContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileTest {

    @Test
    void testCreateFile() {
        File file = new File("readme.md", 50);
        assertEquals("readme.md", file.name());
        assertEquals(NodeType.FILE, file.type());
        assertEquals(50, file.size(SizeContext.empty()));
    }

    @Test
    void testFileSizeIsContentSize() {
        File file = new File("data.bin", 1024);
        assertEquals(1024, file.size(SizeContext.empty()));
    }

    @Test
    void testZeroSize() {
        File file = new File("empty.txt", 0);
        assertEquals(0, file.size(SizeContext.empty()));
    }
}
