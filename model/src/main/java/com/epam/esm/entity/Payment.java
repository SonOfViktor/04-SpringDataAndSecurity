package com.epam.esm.entity;

import com.epam.esm.listener.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "payments", schema = "module_4")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
@Builder
@EntityListeners(AuditListener.class)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int paymentId;

    private LocalDateTime createdDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "payment", cascade = CascadeType.PERSIST)
    private List<UserOrder> orders;

    public Payment(int paymentId, LocalDateTime createdDate, User user) {
        this.paymentId = paymentId;
        this.createdDate = createdDate;
        this.user = user;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate.truncatedTo(ChronoUnit.MILLIS);
    }

    public void setOrders(List<UserOrder> orders) {
        this.orders = orders;
        this.orders.forEach(order -> order.setPayment(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return paymentId == payment.paymentId &&
                Objects.equals(createdDate, payment.createdDate) &&
                Objects.equals(user, payment.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, createdDate, user);
    }

}
