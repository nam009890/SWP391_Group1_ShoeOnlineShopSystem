package Group1.ShoesOnlineShop;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FixFontsTest {

    @Test
    public void runFixFonts() throws Exception {
        Path dir = Paths.get("src/main/resources/templates");
        System.out.println("Starting font replacement in: " + dir.toAbsolutePath());
        
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".html"))
                 .forEach(FixFontsTest::processFile);
        }
        System.out.println("Font Replacement Done!");
    }

    private static void processFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            String original = content;

            // Replacing mojibake strings. These are the literal UTF-8 decoded strings representing the double-encoding
            content = content.replace("â‚«", "₫");
            content = content.replace("â€”", "—");
            content = content.replace("â€¦", "…");
            content = content.replace("â† ", "←");
            content = content.replace("âš ", "⚠️");
            content = content.replace("â­ ", "★");
            content = content.replace("â— ", "●");

            if (!original.equals(content)) {
                Files.write(file, content.getBytes(StandardCharsets.UTF_8));
                System.out.println("Updated " + file.getFileName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
