package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)
    private Order order;
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) //무조건 타입은 STRING으로! ORDINAL로 하면 새로운 ENUM 타입이 생길 시 기존의 ENUM타입이 밀려 XXX로 표시됨!
    private DeliveryStatus status; //READY, COMP
}
