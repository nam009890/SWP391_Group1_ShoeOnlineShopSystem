import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FixFonts {
    public static void main(String[] args) throws Exception {
        Path dir = Paths.get("ShoesOnlineShop/src/main/resources/templates");
        
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".html"))
                 .forEach(FixFonts::processFile);
        }
        System.out.println("Done!");
    }

    private static void processFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            String original = content;

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
