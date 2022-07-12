package com.epam.esm.entity;

import com.epam.esm.listener.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(schema = "module_4", name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
@Builder
@EntityListeners(AuditListener.class)
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int orderId;

    @Positive
    @Digits(integer = 3, fraction = 2)
    private BigDecimal cost;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "gift_certificate_id")
    private GiftCertificate giftCertificate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOrder userOrder = (UserOrder) o;
        return Objects.equals(cost, userOrder.cost) &&
                Objects.equals(payment, userOrder.payment) &&
                Objects.equals(giftCertificate, userOrder.giftCertificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, payment, giftCertificate);
    }
}
