package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성 메서드 외의 다른 방식으로 Order의 생성을 금지
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //fetch 타입은 무조건 LAZY로! -> EAGER로 해놓으면 ORDER를 조회할 때 연관된 MEMBER를 모두 조회! N+1문제
    @ManyToOne(fetch = FetchType.LAZY) // 양방향 연관관계 -> 연관관계의 주인을 결정해줘야 함! 이때 연관관계의 주인은 FK가 가까운 쪽(ORDER)으로
    @JoinColumn(name = "member_id") //FK의 이름이 member_id
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //모든 엔티티는 생성할 때 각자 persist를 해줘야 하지만 cascade옵션을 키면 order만 생성해도 같이 persist가능! 삭제도 같이
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // 일대일 관계에서는 연관관계의 주인을 어디에다 둬도 상관 X, 되도록이면 접근이 많은 쪽으로
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER , CANCEL]

    //==연관관계 편의 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);                       //양방향 연관관계에서 데이터를 집어넣기 위한 메서드 보통 메서드의 위치는 중점적으로 다루는 부분에 위치
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==(생성하는 지점 변경 필요시 요기만 변경하면 됨!)//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
