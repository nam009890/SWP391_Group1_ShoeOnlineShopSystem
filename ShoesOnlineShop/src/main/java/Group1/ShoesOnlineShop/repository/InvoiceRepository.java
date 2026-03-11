package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            SELECT i FROM Invoice i
            WHERE i.isActive = true
            AND (:status IS NULL OR i.isActive = :status)
            AND (:keyword IS NULL OR LOWER(i.order.user.fullName) LIKE LOWER(CONCAT('%',:keyword,'%')))
           """)
    Page<Invoice> searchInvoices(
            @Param("keyword") String keyword,
            @Param("status") Boolean status,
            Pageable pageable
    );

}