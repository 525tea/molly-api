package org.example.mollyapi;

import jakarta.transaction.Transactional;
import org.example.mollyapi.order.dto.OrderRequestDto;
import org.example.mollyapi.order.service.OrderService;
import org.example.mollyapi.product.dto.UploadFile;
import org.example.mollyapi.product.dto.request.ProductItemReqDto;
import org.example.mollyapi.product.dto.request.ProductRegisterReqDto;
import org.example.mollyapi.product.entity.Category;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductItem;
import org.example.mollyapi.product.service.ProductServiceImpl;
import org.example.mollyapi.user.auth.dto.SignInReqDto;
import org.example.mollyapi.user.auth.service.SignInService;
import org.example.mollyapi.user.dto.SignUpReqDto;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.service.SignUpService;
import org.example.mollyapi.user.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    OrderService service;
    @Autowired
    private ProductServiceImpl productServiceImpl;

    @Test
    void OrderTest(){

    }
}
