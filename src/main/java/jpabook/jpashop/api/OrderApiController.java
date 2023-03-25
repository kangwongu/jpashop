package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // 조회 v2, 엔티티 -> dto 반환
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> findOrders = orderRepository.findAll();
        List<OrderDto> response = findOrders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return response;
    }

    // 조회 v3, dto 반환, 페치 조인으로 쿼리 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> findOrders = orderRepository.findAllWithItem();
        List<OrderDto> response = findOrders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return response;
    }

    // 조회 v3.1, dto 반환, 페치 조인 + 배치사이즈로 쿼리 최적화
    @GetMapping("/api/v3.1/orders")
    public Page<OrderDto> ordersV3_page(@RequestParam(defaultValue = "0") int offset,
                                        @RequestParam(defaultValue = "100") int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Order> findOrders = orderRepository.findAllWithMemberDelivery(pageable);

        Page<OrderDto> response = findOrders.map(o -> new OrderDto(o));
        return response;
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
//        private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            orderId = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress();
//            o.getOrderItems().stream().forEach(orderItem -> orderItem.getItem().getName());
//            orderItems = o.getOrderItems();
            orderItems = o.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
