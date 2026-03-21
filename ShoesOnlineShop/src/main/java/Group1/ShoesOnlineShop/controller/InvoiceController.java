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
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


    // LIST INVOICE
    @GetMapping
    public String listInvoices(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "invoiceId") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,

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
    @GetMapping("/view/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {

        Invoice invoice = invoiceService.findById(id);

        model.addAttribute("invoice", invoice);

        return "invoice-detail";
    }


    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable Long id) {

        invoiceService.deleteInvoice(id);

        return "redirect:/invoices";
    }

        // =========================
    // Create Page
    // =========================
    @GetMapping("/create")
    public String createPage(Model model) {

        List<Order> orders = invoiceService.getConfirmOrders();

        model.addAttribute("orders", orders);

        return "invoice-create";
    }

    // =========================
    // Load Order Info
    // =========================
    @GetMapping("/create/{orderId}")
    public String createFromOrder(@PathVariable Long orderId, Model model) {

        Order order = invoiceService.getOrderById(orderId);

        List<Order> orders = invoiceService.getConfirmOrders();

        model.addAttribute("order", order);
        model.addAttribute("orders", orders);

        return "invoice-create";
    }

    // =========================
    // Generate Invoice
    // =========================
    @PostMapping("/create")
    public String createInvoice(@RequestParam Long orderId) {

        invoiceService.generateInvoice(orderId);

        return "redirect:/invoices";
    }
    
    @GetMapping("/detail/{id}")
public String invoiceDetail(@PathVariable Long id, Model model){

    Invoice invoice = invoiceService.findById(id);

    model.addAttribute("invoice", invoice);

    return "invoice-detail";
}
}