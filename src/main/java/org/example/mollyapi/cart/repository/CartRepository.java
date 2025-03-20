package org.example.mollyapi.cart.repository;

import org.example.mollyapi.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>, CartCustomRepository{
    Cart findByProductItemIdAndUserUserId(Long itemId, Long userId);
    Optional<Cart> findByCartIdAndUserUserId(Long cartId, Long userId);
    Optional<Cart> findById(Long cartId); // cartId로 장바구니 조회
    void deleteByCartId(Long cartId); // cartId 기반 삭제
    boolean existsByProductItemIdAndUserUserId(Long itemId, Long userId);
}
