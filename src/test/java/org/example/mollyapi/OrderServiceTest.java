package org.example.mollyapi;

import lombok.extern.slf4j.Slf4j;
import org.example.mollyapi.order.entity.Order;
import org.example.mollyapi.order.repository.OrderRepository;
import org.example.mollyapi.order.service.OrderService;
import org.example.mollyapi.order.type.OrderStatus;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        // DB에서 User 조회 (id=1)
        testUser = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // DB에서 주문 조회 (tossOrderId 사용)
        testOrder = orderRepository.findByTossOrderId("ORD-20250213132457-6375")
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        log.info("테스트 유저: {} , 포인트: {} " ,testUser.getNickname(), testUser.getPoint());
        log.info("테스트 주문: {}, 상태: {} " ,testOrder.getTossOrderId(), testOrder.getStatus());
    }

    @Test
    void testSuccessOrder() {
        // Given: 결제 성공 정보
        String fakePaymentId = "PAY-12345";
        String fakePaymentType = "CARD";
        Long fakePaymentAmount = 258000L;
        Integer fakePointUsage = 5000;

        // When: successOrder() 실행
        orderService.successOrder("ORD-20250213132457-6375", fakePaymentId, fakePaymentType, fakePaymentAmount, fakePointUsage);

        // Then: 주문 상태 변경 확인
        assertEquals(OrderStatus.SUCCEEDED, testOrder.getStatus());
        assertEquals(fakePaymentId, testOrder.getPaymentId());
        assertEquals(fakePaymentType, testOrder.getPaymentType());
        assertEquals(fakePaymentAmount, testOrder.getPaymentAmount());
        assertEquals(fakePointUsage, testOrder.getPointUsage());

        // 포인트 차감 확인
        assertEquals(100000 - fakePointUsage, testUser.getPoint());  // 원래 포인트에서 차감됐는지 확인

        // 테스트 결과 출력
        log.info("successOrder 테스트 결과:");
        log.info("Order status: {}", testOrder.getStatus());
        log.info("Order payment 정보: paymentId={}, paymentType={}, paymentAmount={}", testOrder.getPaymentId(), testOrder.getPaymentType(), testOrder.getPaymentAmount());
        log.info("Order point_usage: {}", testOrder.getPointUsage());
        log.info("User point: {}", testUser.getPoint());
    }
}