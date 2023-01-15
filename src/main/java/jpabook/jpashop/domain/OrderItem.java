package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    /**
     * 생성 메소드
     * OrderItem 객체를 생성할 때, 외부에서 값을 세팅하며 생성하지 않고, 생성 메소드를 하나 파서 묶어놓는 것 (추후 수정 시, 생성 메소드만 수정하면 됨)
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 주문한 수량만큼 상품의 재고에서 뺀다
        item.removeStock(count);
        return orderItem;
    }

    /**
     * 비즈니스 로직
     */
    public void registerOrder(Order order) {
        this.order = order;
    }

    // 주문 취소
    public void cancel() {
        getItem().addStock(count);  // 주문했던 수량만큼 원복시킴
    }

    /**
     * 조회 로직
     */
    // 주문 총액 반환
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
