package org.example.mollyapi.order.service;

import lombok.RequiredArgsConstructor;
import org.example.mollyapi.order.dto.OrderRequestDto;
import org.example.mollyapi.order.dto.OrderResponseDto;
import org.example.mollyapi.order.entity.*;
import org.example.mollyapi.order.repository.OrderRepository;
import org.example.mollyapi.order.repository.OrderDetailRepository;
import org.example.mollyapi.order.type.OrderStatus;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductItem;
import org.example.mollyapi.product.repository.ProductItemRepository;
import org.example.mollyapi.product.repository.ProductRepository;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final UserRepository userRepository;

    public OrderResponseDto createOrder(Long userId, List<OrderRequestDto> orderRequests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        // 새로운 주문 생성 (초기 상태는 PENDING)
        Order order = Order.builder()
                .user(user)
                .paymentAmount(0L)  // 초기값 0원
                .status(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        List<OrderDetail> orderDetails = orderRequests.stream().map(req -> {
            // 비관적 락을 걸어 Product 조회
            Product product = productRepository.findWithLockById(req.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId=" + req.getProductId()));

            // ProductItem을 Repository에서 직접 조회
            ProductItem productItem = productItemRepository.findById(req.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 아이템을 찾을 수 없습니다. itemId=" + req.getItemId()));

            // 재고 체크
            if (productItem.getQuantity() < req.getQuantity()) {
                order.markAsFailed();  // 주문 실패 처리
                throw new IllegalArgumentException("재고가 부족하여 주문이 불가능합니다. itemId=" + req.getItemId());
            }

            // 재고 감소
            productItem.decreaseStock(req.getQuantity());
            productItemRepository.save(productItem);  // 감소한 재고 저장

            return OrderDetail.builder()
                    .order(order)
                    .productItem(productItem)
                    .price(product.getPrice())
                    .quantity(req.getQuantity())
                    .brandName(product.getBrandName())
                    .productName(product.getProductName())
                    .build();
        }).collect(Collectors.toList());

        // 주문 상세(OrderDetail) 저장
        orderDetailRepository.saveAll(orderDetails);

        // 총 결제 금액 계산 후 Order에 반영(생각좀)
        long totalAmount = orderDetails.stream()
                .mapToLong(d -> d.getPrice() * d.getQuantity())
                .sum();
        order.setPaymentAmount(totalAmount);
        orderRepository.save(order);

        return new OrderResponseDto(order, orderDetails);
    }

}