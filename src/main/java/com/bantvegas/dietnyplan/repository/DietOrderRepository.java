package com.bantvegas.dietnyplan.repository;

import com.bantvegas.dietnyplan.entity.DietOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietOrderRepository extends JpaRepository<DietOrder, Long> {
    Optional<DietOrder> findByEmail(String email);
    Optional<DietOrder> findByToken(String token);
    Optional<DietOrder> findByStripeSessionId(String sessionId);
}
