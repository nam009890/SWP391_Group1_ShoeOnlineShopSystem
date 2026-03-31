package Group1.ShoesOnlineShop;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
public class DumpBytesTest {
    @Test
    public void dumpBytes() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/templates/admin/admin-product-list.html"));
        String content = new String(bytes, "UTF-8");
        int index = content.indexOf("Price (");
        if (index != -1) {
            System.out.println("Found around index: " + index);
            int start = Math.max(0, index - 10);
            int end = Math.min(content.length(), index + 30);
            String sub = content.substring(start, end);
            System.out.println("Substring: " + sub);
            for (char c : sub.toCharArray()) {
                System.out.printf("Char: %c, Hex: %04X%n", c, (int)c);
            }
        } else {
            System.out.println("Not found");
        }
    }
}
