package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 연관관계 주인
    private Member member;

    // Order가 Delivery의 생명주기를 관리
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // Order가 OrderItem의 생명주기를 관리
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    // 주문상태 (Enum)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * 연관관계 편의 메소드
     */
    // Order - Member
    public void registerMember(Member member) {
        this.member = member;
        member.getOrder().add(this);
    }

    // Order - OrderItem
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.registerOrder(this);
    }

    // Order - Delivery
    public void registerDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.registerOrder(this);
    }

    /**
     * 생성 메소드
     * Order 객체를 생성할 때는 OrderItem, Member, Delivery등 연관관계가 복잡하기 때문에 아예 생성메소드를 하나 파는것이 좋다
     */
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

    /**
     * 비즈니스 로직
     */
    // 주문 취소
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        // 주문했던 수량 원복
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /**
     * 조회 로직
     */
    // 전체 주문가격 조회
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


}
