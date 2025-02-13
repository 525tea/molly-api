package org.example.mollyapi.cart.repository;

import org.example.mollyapi.cart.dto.Response.CartInfoResDto;

import java.util.List;

public interface CartCustomRepository {
    List<CartInfoResDto> getCartInfo(Long userId);
}
