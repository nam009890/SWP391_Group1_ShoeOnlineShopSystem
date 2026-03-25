package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_coupons", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "is_used")
    private boolean isUsed = false;
}
