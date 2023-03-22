package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 페치 조인
    @Query("select o from Order o join fetch o.member m join fetch o.delivery")
    List<Order> findAllWithMemberDelivery();

//    @Query(value = "select o from Order o join o.member m" +
//                    " where o.status = :status " +
//                    " and m.name like :name " +
//                    " limit 1000")
//    List<Order> findAll(OrderSearch orderSearch, @Param("status") OrderStatus orderStatus, @Param("name") String memberName);


}
