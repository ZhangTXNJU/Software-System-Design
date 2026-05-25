import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FileSystem fs = new FileSystem();
        CommandParser parser = new CommandParser(fs);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String output = parser.execute(line);
                if (output != null) {
                    System.out.println(output);
                }
            }
        }
    }
}
