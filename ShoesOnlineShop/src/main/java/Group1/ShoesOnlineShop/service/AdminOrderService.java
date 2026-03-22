package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.repository.AdminOrderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminOrderService {

    @Autowired
    private AdminOrderRepository adminOrderRepository;

    // === GET LIST WITH FILTER & PAGINATION ===
    public Page<Order> getOrders(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderDate").descending());

        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("orderStatus"), status));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate searchPredicate;
                try {
                    Long orderId = Long.parseLong(keyword.trim());
                    searchPredicate = cb.or(
                            cb.equal(root.get("orderId"), orderId),
                            cb.like(cb.lower(root.get("phone")), likePattern),
                            cb.like(cb.lower(root.join("user").get("fullName")), likePattern)
                    );
                } catch (NumberFormatException e) {
                    searchPredicate = cb.or(
                            cb.like(cb.lower(root.get("phone")), likePattern),
                            cb.like(cb.lower(root.join("user").get("fullName")), likePattern)
                    );
                }
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return adminOrderRepository.findAll(spec, pageable);
    }

    // === GET BY ID ===
    public Order getOrderById(Long id) {
        return adminOrderRepository.findById(id).orElse(null);
    }

    // === RECENT ORDERS FOR DASHBOARD ===
    public List<Order> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("orderDate").descending());
        return adminOrderRepository.findAll(pageable).getContent();
    }

    // === COUNT ORDERS ===
    public long countAllOrders() {
        return adminOrderRepository.countAllOrders();
    }

    // === REVENUE FOR FINANCIAL REPORT ===
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return adminOrderRepository.sumRevenueByDateRange(start, end);
    }

    public long countOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return adminOrderRepository.countOrdersByDateRange(start, end);
    }

    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return adminOrderRepository.findByDateRange(start, end);
    }
}
