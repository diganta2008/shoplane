package com.shoplane.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByIdAndActiveTrue(Long id);

    /**
     * Atomic conditional stock decrement. Returns the affected row count —
     * used by the order flow to guard against over-selling under concurrency.
     */
    @Modifying
    @Query("update Product p set p.stock = p.stock - :qty where p.id = :id and p.stock >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);
}
