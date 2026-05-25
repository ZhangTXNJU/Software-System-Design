import java.util.HashSet;
import java.util.Set;

public class SizeContext {
    public final Set<Node> visited;
    public final boolean followLinks;

    public SizeContext(Set<Node> visited, boolean followLinks) {
        this.visited = visited;
        this.followLinks = followLinks;
    }

    public static SizeContext empty() {
        return new SizeContext(new HashSet<>(), false);
    }
}
