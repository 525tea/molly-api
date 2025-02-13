package org.example.mollyapi.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.mollyapi.cart.dto.AddCartReqDto;
import org.example.mollyapi.cart.dto.CartInfoResDto;
import org.example.mollyapi.cart.entity.Cart;
import org.example.mollyapi.cart.repository.CartRepository;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.product.entity.ProductItem;
import org.example.mollyapi.product.repository.ProductItemRepository;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.mollyapi.common.exception.error.impl.CartError.*;
import static org.example.mollyapi.common.exception.error.impl.ProductItemError.*;
import static org.example.mollyapi.common.exception.error.impl.UserError.NOT_EXISTS_USER;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRep;
    private final ProductItemRepository productItemRep;
    private final CartRepository cartRep;

    /**
     * 장바구니에 상품 담기 기능
     * @param addCartReqDto 추가하려는 데이터
     * @param userId     사용자 PK
     */
    @Transactional
    public ResponseEntity<?> addCart(AddCartReqDto addCartReqDto, Long userId) {
        // 1. 가입된 사용자 여부 체크
        User user = userRep.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        // 2. 상품 존재 여부 체크
        ProductItem item = productItemRep.findById(addCartReqDto.itemId())
                .orElseThrow(() -> new CustomException(NOT_EXISTS_ITEM));

        // 3. 상품의 재고가 남아 있는 지 체크
        if(item.getQuantity() == 0)
            throw new CustomException(SOLD_OUT);

        // 4. 장바구니에 동일한 상품이 담겨 있는 지 체크
        Cart cart = cartRep.findByProductItemIdAndUserUserId(addCartReqDto.itemId(), userId)
                .orElseGet(() -> insertNewCart(addCartReqDto, user, item)); // 5. 존재하지 않으면 삽입

        // 6. 초과 수량을 장바구니에 담는 지 체크
        long totalQuantity = cart.getQuantity() + addCartReqDto.quantity(); //기존에 담아둔 수량 + 추가 하려는 수량
        if(totalQuantity > item.getQuantity()) //재고 수량을 초과할 경우
            throw new CustomException(OVER_QUANTITY);

        // 6-1. 수량 업데이트
        try {
            cart.updateQuantity(totalQuantity);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            throw new CustomException(FAIL_UPDATE); // 수량 업데이트 실패
        }
    }

    /**
     * 새로운 상품 장바구니에 추가
     * @param cartReqDto 추가하려는 데이터
     * @param user 사용자 정보
     * @param item 상품 정보
     * @return Cart 반환
     */
    public Cart insertNewCart(AddCartReqDto cartReqDto, User user, ProductItem item) {
        // 재고 수량 초과 체크
        if(item.getQuantity() < cartReqDto.quantity())
            throw new CustomException(OVER_QUANTITY);

        // 장바구니 최대 수량(30개) 미만 체크
        int count = cartRep.countByUserUserId(user.getUserId());
        if(count > 30)
            throw new CustomException(MAX_CART);

        // 새로운 Cart 엔티티 생성
        Cart newCart = Cart.builder()
                .quantity(cartReqDto.quantity())
                .user(user)
                .productItem(item)
                .build();

        return cartRep.save(newCart);
    }

    /**
     * 장바구니 조회 기능
     * @param userId 사용자 PK
     * */
    public ResponseEntity<?> getCartDetail(Long userId) {
        // 1. 가입된 사용자 여부 체크
        boolean exists = userRep.existsById(userId);
        if(!exists) throw new CustomException(NOT_EXISTS_USER);

        // 2. 사용자 장바구니 조회
        List<CartInfoResDto> cartInfoList = cartRep.getCartInfo(userId);
        if(cartInfoList.isEmpty()) throw new CustomException(EMPTY_CART);

        return ResponseEntity.ok(cartInfoList);
    }

    /**
     * 장바구니 내역 삭제 기능
     * */
    @Transactional
    public void deleteCartItem(List<Long> cartList, Long userId) {
        // 1. 가입된 사용자 여부 체크
        boolean existsUser = userRep.existsById(userId);
        if(!existsUser) throw new CustomException(NOT_EXISTS_USER);

        for (Long cartId : cartList) {
            try {
                boolean existsCart = cartRep.existsByCartIdAndUserUserId(cartId, userId);
                if(!existsCart) throw new CustomException(NOT_EXIST_CART);

                cartRep.deleteByCartIdAndUserUserId(cartId, userId);
            } catch (CustomException e) {
                throw new CustomException(FAIL_DELETE);
            }
        }
    }
}
