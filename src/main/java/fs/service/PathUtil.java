package fs.service;

import java.util.ArrayList;
import java.util.List;

public final class PathUtil {

    private PathUtil() {}

    public static boolean validate(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (!path.startsWith("/")) {
            return false;
        }
        // root is valid
        if (path.equals("/")) {
            return true;
        }
        // trailing slash (not root) is invalid
        if (path.endsWith("/")) {
            return false;
        }
        // consecutive slashes
        if (path.contains("//")) {
            return false;
        }
        // check each segment
        String[] segments = path.substring(1).split("/");
        for (String seg : segments) {
            if (seg.isEmpty() || seg.equals(".") || seg.equals("..")) {
                return false;
            }
        }
        return true;
    }

    public static List<String> split(String path) {
        List<String> parts = new ArrayList<>();
        if (path.equals("/")) {
            return parts;
        }
        String[] segments = path.substring(1).split("/");
        for (String seg : segments) {
            parts.add(seg);
        }
        return parts;
    }
}
