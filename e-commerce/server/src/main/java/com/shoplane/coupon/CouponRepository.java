package com.shoplane.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, String> {

    @Query("""
        select c from Coupon c
         where c.code = :code
           and c.active = true
           and (c.validFrom  is null or c.validFrom  <= :today)
           and (c.validUntil is null or c.validUntil >= :today)
        """)
    Optional<Coupon> findActive(@Param("code") String code, @Param("today") LocalDate today);
}
