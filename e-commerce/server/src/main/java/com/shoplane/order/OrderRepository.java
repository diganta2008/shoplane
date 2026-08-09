package com.shoplane.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByPlacedAtDesc(Long userId, Pageable pageable);
    Optional<Order> findByOrderNumberAndUserId(String orderNumber, Long userId);
}
