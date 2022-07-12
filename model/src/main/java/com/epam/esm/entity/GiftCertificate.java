package com.epam.esm.entity;

import com.epam.esm.listener.AuditListener;
import com.epam.esm.validategroup.ForCreate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Table(name = "gift_certificates", schema = "module_4")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@EntityListeners(AuditListener.class)
public class GiftCertificate {
    private static final String DATE_JSON_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int giftCertificateId;

    @NotBlank(groups = ForCreate.class)
    @Size(min = 3, max = 45)
    private String name;

    @NotBlank(groups = ForCreate.class)
    @Size(max = 500)
    private String description;

    @Positive
    @Digits(integer = 3, fraction = 2)
    private BigDecimal price;

    @Positive(groups = ForCreate.class)
    @Digits(integer = 2, fraction = 0)
    private int duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_JSON_PATTERN)
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_JSON_PATTERN)
    private LocalDateTime lastUpdateDate;

    @Builder.Default
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "gift_certificate_tag", schema = "module_4",
            joinColumns = @JoinColumn(name = "gift_certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "giftCertificate")
    private List<UserOrder> userOrders;

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate.truncatedTo(ChronoUnit.MILLIS);
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate.truncatedTo(ChronoUnit.MILLIS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftCertificate that = (GiftCertificate) o;
        return giftCertificateId == that.giftCertificateId && duration == that.duration &&
                Objects.equals(name, that.name) && Objects.equals(description, that.description) &&
                Objects.equals(price, that.price) && Objects.equals(createDate, that.createDate) &&
                Objects.equals(lastUpdateDate, that.lastUpdateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(giftCertificateId, name, description, price, duration, createDate, lastUpdateDate);
    }

    @Override
    public String toString() {
        return  "\nGiftCertificate #" + giftCertificateId +
                " " + name + "\n" +
                "description " + description + "\n" +
                "duration " + duration + " day(s)" +
                ", price " + price +
                ", createDate " + createDate +
                ", lastUpdateDate " + lastUpdateDate;
    }
}
