package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // 조회 v1, 엔티티 직접 반환
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> findOrders = orderRepository.findAll();
        for (Order findOrder : findOrders) {
            findOrder.getMember().getName();        // LAZY 초기화
            findOrder.getDelivery().getAddress();   // LAZY 초기화
            List<OrderItem> orderItems = findOrder.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();      // LAZY 초기화
            }
//            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return findOrders;
    }
}
