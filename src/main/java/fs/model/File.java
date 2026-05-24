package fs.model;

import fs.context.SizeContext;

public class File implements Node {
    private final String name;
    private final long contentSize;

    public File(String name, long contentSize) {
        this.name = name;
        this.contentSize = contentSize;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public NodeType type() {
        return NodeType.FILE;
    }

    @Override
    public long size(SizeContext ctx) {
        return contentSize;
    }
}
