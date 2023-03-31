package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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
                .collect(toList());
        return response;
    }

    // 조회 v3, dto 반환, 페치 조인으로 쿼리 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> findOrders = orderRepository.findAllWithItem();
        List<OrderDto> response = findOrders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
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

    // 조회 v4, dto로 바로 조회 후 반환
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        List<OrderQueryDto> result = orderQueryRepository.findOrderQueryDtos();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = orderQueryRepository.findOrderItems(o.getOrderId());   // N번 실행
            o.setOrderItems(orderItems);
        });
        return result;
    }

    // 조회 v5, dto로 바로 조회 후 반환 (최적화)
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        // Order에 관련된 데이터 우선 조회
        List<OrderQueryDto> result = orderQueryRepository.findOrderQueryDtos();

        // Order의 id만 추출
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(toList());

        // Order의 id로 해당하는 OrderItem 데이터 조회 (쿼리를 1번만 날림, v4는 N번, v5는 1번)
        List<OrderItemQueryDto> orderItems = orderQueryRepository.findAllByDtoOptimization(orderIds);

        // OrderItem을 Order에 세팅하기 위한 준비 (한번에 데이터를 가져온 뒤, 메모리에서 세팅해주는 방식)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        // Order에 OrderItem 세팅
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    // 조회 v6, dto로 바로 조회 후 반환 (최적화2)
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDtoFlat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
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
                    .collect(toList());
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
