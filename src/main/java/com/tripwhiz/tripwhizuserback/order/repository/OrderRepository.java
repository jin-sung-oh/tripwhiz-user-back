package com.tripwhiz.tripwhizuserback.order.repository;

import com.tripwhiz.tripwhizuserback.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByMemberEmail(String email, Pageable pageable);
}
