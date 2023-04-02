package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    // 페치 조인
    @Query("select o from Order o join fetch o.member m join fetch o.delivery")
    List<Order> findAllWithMemberDelivery();

    // 페치 조인
    @Query(value = "select o from Order o " +
            "join fetch o.member m " +
            "join fetch o.delivery d",
           countQuery = "select count(o) from Order o")
    Page<Order> findAllWithMemberDelivery(Pageable pageable);

    // 페치 조인
    @Query("select distinct o from Order o " +
            "join fetch o.member m " +
            "join fetch o.delivery d " +
            "join fetch o.orderItems oi " +
            "join fetch oi.item i")
    List<Order> findAllWithItem();

}
