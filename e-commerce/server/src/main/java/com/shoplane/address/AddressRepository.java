package com.shoplane.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    Optional<Address> findFirstByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.userId = :userId AND a.id <> :keepId")
    void clearOtherDefaults(@Param("userId") Long userId, @Param("keepId") Long keepId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.userId = :userId")
    void clearAllDefaults(@Param("userId") Long userId);
}
