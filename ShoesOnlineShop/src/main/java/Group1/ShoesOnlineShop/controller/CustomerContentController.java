package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CustomerContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/articles")
    public String listArticles(Model model) {
        // Get all active contents
        Page<Content> contentPage = contentService.getContents("", null, true, 1, 50);
        model.addAttribute("articles", contentPage.getContent());
        return "customer-content-list";
    }

    @GetMapping("/articles/{id}")
    public String articleDetail(@PathVariable(name = "id") Long id, Model model) {
        Content content = contentService.getContentById(id);
        if (content == null) {
            return "redirect:/articles";
        }
        model.addAttribute("content", content);
        return "customer-content-detail";
    }
}
