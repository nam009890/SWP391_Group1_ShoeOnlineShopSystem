package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Invoice;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.InvoiceService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/internal/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // LIST INVOICE
    @GetMapping
    public String listInvoices(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", defaultValue = "invoiceId") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        Page<Invoice> invoices =
                invoiceService.getInvoices(keyword, status, page, size, sort);

        model.addAttribute("invoices", invoices);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        return "invoice-list";
    }

    // VIEW DETAIL
    @GetMapping("/detail/{id}")
    public String invoiceDetail(@PathVariable(name = "id") Long id, Model model) {
        Invoice invoice = invoiceService.findById(id);
        model.addAttribute("invoice", invoice);
        return "invoice-detail";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable(name = "id") Long id) {
        invoiceService.deleteInvoice(id);
        return "redirect:/internal/invoices";
    }

    // Create Page
    @GetMapping("/create")
    public String createPage(Model model) {
        List<Order> orders = invoiceService.getConfirmOrders();
        model.addAttribute("orders", orders);
        return "invoice-create";
    }

    // Load Order Info
    @GetMapping("/create/{orderId}")
    public String createFromOrder(@PathVariable(name = "orderId") Long orderId, Model model) {
        Order order = invoiceService.getOrderById(orderId);
        List<Order> orders = invoiceService.getConfirmOrders();
        model.addAttribute("order", order);
        model.addAttribute("orders", orders);
        return "invoice-create";
    }

    // Generate Invoice
    @PostMapping("/create")
    public String createInvoice(@RequestParam(name = "orderId") Long orderId) {
        invoiceService.generateInvoice(orderId);
        return "redirect:/internal/invoices";
    }

    // TOGGLE STATUS
    @PostMapping("/toggle")
    public String toggleStatus(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "status") String status) {
        invoiceService.toggleStatus(id, status);
        return "redirect:/internal/invoices";
    }
}