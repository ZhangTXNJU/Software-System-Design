package fs.model;

import fs.context.SizeContext;

public interface Node {
    String name();
    NodeType type();
    long size(SizeContext ctx);
}
