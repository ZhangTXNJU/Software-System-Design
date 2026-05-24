package fs.model;

import fs.context.SizeContext;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Directory implements Node {
    private final String name;
    private final Map<String, Node> children;

    public Directory(String name) {
        this.name = name;
        this.children = new TreeMap<>();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public NodeType type() {
        return NodeType.DIRECTORY;
    }

    @Override
    public long size(SizeContext ctx) {
        long total = 0;
        for (Node child : children.values()) {
            total += child.size(ctx);
        }
        return total;
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public void putChild(String name, Node node) {
        children.put(name, node);
    }

    public void removeChild(String name) {
        children.remove(name);
    }

    public Collection<Node> listChildren() {
        return children.values();
    }
}
