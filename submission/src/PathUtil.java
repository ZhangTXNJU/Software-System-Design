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
        if (path.equals("/")) {
            return true;
        }
        if (path.endsWith("/")) {
            return false;
        }
        if (path.contains("//")) {
            return false;
        }
        String[] segments = path.substring(1).split("/");
        for (String seg : segments) {
            if (seg.isEmpty() || seg.equals(".") || seg.equals("..")) {
                return false;
            }
            for (int i = 0; i < seg.length(); i++) {
                char c = seg.charAt(i);
                if (c <= 32 || c == 127) {
                    return false;
                }
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
