package Group1.ShoesOnlineShop;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class AddMetaCharsetTest {

    @Test
    public void addMeta() throws Exception {
        Path dir = Paths.get("src/main/resources/templates");
        
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".html"))
                 .forEach(AddMetaCharsetTest::processFile);
        }
        System.out.println("Meta Charset Addition Done!");
    }

    private static void processFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            
            // Only process if it doesn't already have meta charset UTF-8
            if (!content.toLowerCase().contains("meta charset")) {
                // Find <head>
                int headIndex = content.toLowerCase().indexOf("<head>");
                if (headIndex != -1) {
                    int insertIndex = headIndex + "<head>".length();
                    String newContent = content.substring(0, insertIndex) + "\n    <meta charset=\"UTF-8\">" + content.substring(insertIndex);
                    Files.write(file, newContent.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Added meta charset to " + file.getFileName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
