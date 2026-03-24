package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = """
            SELECT DISTINCT i FROM Invoice i
            JOIN FETCH i.order o
            JOIN FETCH o.user
            LEFT JOIN FETCH o.orderDetails od
            LEFT JOIN FETCH od.product
            LEFT JOIN FETCH o.coupon
            WHERE i.isActive = true
            AND (:status IS NULL OR i.status = :status)
            AND (:keyword IS NULL OR LOWER(o.user.fullName) LIKE LOWER(CONCAT('%',:keyword,'%')))
           """,
           countQuery = """
            SELECT COUNT(DISTINCT i) FROM Invoice i
            JOIN i.order o
            JOIN o.user
            WHERE i.isActive = true
            AND (:status IS NULL OR i.status = :status)
            AND (:keyword IS NULL OR LOWER(o.user.fullName) LIKE LOWER(CONCAT('%',:keyword,'%')))
           """)
    Page<Invoice> searchInvoices(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("""
            SELECT i FROM Invoice i
            JOIN FETCH i.order o
            JOIN FETCH o.user
            LEFT JOIN FETCH o.orderDetails od
            LEFT JOIN FETCH od.product
            LEFT JOIN FETCH o.coupon
            WHERE i.invoiceId = :id
           """)
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);
}