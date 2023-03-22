package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderSimpleQueryRepository extends JpaRepository<Order, Long> {
    // Dto로 바로 조회
    @Query("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
            "from Order o join o.member m join o.delivery d")
    List<OrderSimpleQueryDto> findOrderDtos();
}
