import java.util.List;

public class CommandParser {
    private final FileSystem fs;

    public CommandParser(FileSystem fs) {
        this.fs = fs;
    }

    public String execute(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.trim().split("\\s+");
        String cmd = parts[0];

        switch (cmd) {
            case "MKDIR":
                if (parts.length >= 2) {
                    fs.mkdir(parts[1]);
                }
                return null;
            case "TOUCH":
                if (parts.length >= 3) {
                    try {
                        if(parts[2].startsWith("-")){
                            parts[2] = "0";
                        }
                        long size = Long.parseLong(parts[2]);
                        fs.touch(parts[1], size);
                    } catch (NumberFormatException ignored) {
                    }
                }
                return null;
            case "LS":
                if (parts.length >= 2) {
                    List<String> result = fs.ls(parts[1]);
                    if (result != null && !result.isEmpty()) {
                        return String.join("\n", result);
                    }
                }
                return null;
            case "INFO":
                if (parts.length >= 2) {
                    Long size = fs.info(parts[1]);
                    if (size != null) {
                        return size.toString();
                    }
                }
                return null;
            default:
                return null;
        }
    }
}
