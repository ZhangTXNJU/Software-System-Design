package fs.service;

import fs.model.Directory;
import fs.model.File;
import fs.model.Node;
import fs.model.NodeType;
import fs.context.SizeContext;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private final Directory root;

    public FileSystem() {
        this.root = new Directory("/");
    }

    public Node resolve(String absPath) {
        if (!PathUtil.validate(absPath)) {
            return null;
        }
        if (absPath.equals("/")) {
            return root;
        }
        List<String> parts = PathUtil.split(absPath);
        Node current = root;
        for (int i = 0; i < parts.size(); i++) {
            if (current.type() != NodeType.DIRECTORY) {
                return null;
            }
            Directory dir = (Directory) current;
            Node child = dir.getChild(parts.get(i));
            if (child == null) {
                // parent does not exist (or intermediate path missing)
                return null;
            }
            current = child;
        }
        return current;
    }

    private Node resolveParent(String absPath) {
        if (!PathUtil.validate(absPath)) {
            return null;
        }
        if (absPath.equals("/")) {
            return null; // root has no parent
        }
        List<String> parts = PathUtil.split(absPath);
        if (parts.isEmpty()) {
            return root;
        }
        // Walk to the parent directory
        Node current = root;
        for (int i = 0; i < parts.size() - 1; i++) {
            if (current.type() != NodeType.DIRECTORY) {
                return null;
            }
            Directory dir = (Directory) current;
            Node child = dir.getChild(parts.get(i));
            if (child == null || child.type() != NodeType.DIRECTORY) {
                return null;
            }
            current = child;
        }
        return current;
    }

    public void mkdir(String absPath) {
        if (!PathUtil.validate(absPath)) {
            return;
        }
        Node parent = resolveParent(absPath);
        if (parent == null || parent.type() != NodeType.DIRECTORY) {
            return;
        }
        List<String> parts = PathUtil.split(absPath);
        String name = parts.get(parts.size() - 1);
        Directory dir = (Directory) parent;
        Node existing = dir.getChild(name);
        if (existing != null && existing.type() == NodeType.DIRECTORY) {
            // already a directory — no-op
            return;
        }
        // put覆盖: replaces File with Directory
        dir.putChild(name, new Directory(name));
    }

    public void touch(String absPath, long size) {
        if (!PathUtil.validate(absPath)) {
            return;
        }
        Node parent = resolveParent(absPath);
        if (parent == null || parent.type() != NodeType.DIRECTORY) {
            return;
        }
        List<String> parts = PathUtil.split(absPath);
        String name = parts.get(parts.size() - 1);
        Directory dir = (Directory) parent;
        // put覆盖: replaces existing File or Directory
        dir.putChild(name, new File(name, size));
    }

    public List<String> ls(String absPath) {
        List<String> result = new ArrayList<>();
        if (!PathUtil.validate(absPath)) {
            return null;
        }
        Node target = resolve(absPath);
        if (target == null) {
            return null;
        }
        if (target.type() == NodeType.FILE) {
            result.add(target.name());
        } else {
            Directory dir = (Directory) target;
            for (Node child : dir.listChildren()) {
                result.add(child.name());
            }
            // TreeMap already maintains sorted order, but ensure it
            result.sort(String::compareTo);
        }
        return result;
    }

    public Long info(String absPath) {
        if (!PathUtil.validate(absPath)) {
            return null;
        }
        Node target = resolve(absPath);
        if (target == null) {
            return null;
        }
        return target.size(SizeContext.empty());
    }
}
