public interface Node {
    String name();
    NodeType type();
    long size(SizeContext ctx);
}
