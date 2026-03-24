package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Invoice;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.entity.OrderDetail;
import Group1.ShoesOnlineShop.repository.InvoiceRepository;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          OrderRepository orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
    }

    // LIST + SEARCH + FILTER + SORT
    public Page<Invoice> getInvoices(
            String keyword,
            String status,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sort).ascending());

        if (keyword != null && keyword.isEmpty()) {
            keyword = null;
        }
        if (status != null && status.isEmpty()) {
            status = null;
        }

        return invoiceRepository.searchInvoices(keyword, status, pageable);
    }


    // FIND BY ID (with JOIN FETCH for detail page)
    public Invoice findById(Long id) {
        return invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }


    // DELETE (soft delete)
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setIsActive(false);
        invoiceRepository.save(invoice);
    }

    // Get confirmed orders for create page
    public List<Order> getConfirmOrders() {
        return orderRepository.findByOrderStatus("CONFIRMED");
    }

    // Get order by id with all details (for create page preview)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithDetails(id).orElse(null);
    }

    // generate invoice từ order
    public Invoice generateInvoice(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        // chỉ cho CONFIRM
        if (!"CONFIRMED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new RuntimeException("Only CONFIRM orders can generate invoice");
        }

        // tính tổng tiền từ order detail
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDetail detail : order.getOrderDetails()) {
            BigDecimal subtotal = detail.getUnitPrice()
                    .multiply(BigDecimal.valueOf(detail.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setTotalAmount(totalAmount);
        invoice.setGeneratedDate(LocalDateTime.now());
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setIsActive(true);
        invoice.setStatus("Active");

        return invoiceRepository.save(invoice);
    }

    // sinh mã invoice
    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    // toggle status
    public void toggleStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus(status);
        invoiceRepository.save(invoice);
    }

    // save invoice
    public void save(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    // danh sách invoice
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}