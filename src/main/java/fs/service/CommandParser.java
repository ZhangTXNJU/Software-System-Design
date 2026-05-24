package fs.service;

import java.util.List;

public class CommandParser {
    private final FileSystem fs;

    public CommandParser(FileSystem fs) {
        this.fs = fs;
    }

    /**
     * Parse and execute a single command line.
     * @return output string to print, or null if no output (silent)
     */
    public String execute(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split(" ");
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
                        long size = Long.parseLong(parts[2]);
                        fs.touch(parts[1], size);
                    } catch (NumberFormatException ignored) {
                        // invalid size → silent ignore
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
