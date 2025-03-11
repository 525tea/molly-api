package org.example.mollyapi.cart.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.mollyapi.MollyApiApplication;
import org.example.mollyapi.cart.dto.Request.AddCartReqDto;
import org.example.mollyapi.cart.dto.Request.UpdateCartReqDto;
import org.example.mollyapi.cart.dto.Response.CartInfoDto;
import org.example.mollyapi.cart.dto.Response.CartInfoResDto;
import org.example.mollyapi.cart.entity.Cart;
import org.example.mollyapi.cart.repository.CartRepository;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.product.dto.ProductFilterCondition;
import org.example.mollyapi.product.dto.UploadFile;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductImage;
import org.example.mollyapi.product.entity.ProductItem;
import org.example.mollyapi.product.repository.ProductImageRepository;
import org.example.mollyapi.product.repository.ProductItemRepository;
import org.example.mollyapi.product.repository.ProductRepository;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.example.mollyapi.user.type.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static reactor.core.publisher.Mono.create;
import static reactor.core.publisher.Mono.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class CartServiceTest {
    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductItemRepository productItemRepository;

    @DisplayName("장바구니에 새로운 상품을 담는다.")
    @Test
    void shouldAddNewProductItemToCart() {
        // given
        Long userId = 2L;
        Long quantity = 3L;
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        AddCartReqDto addCartReqDto = new AddCartReqDto(testItem.getId(), quantity);

        //when
        Cart cart = cartRepository.findByProductItemIdAndUserUserId(testItem.getId(), userId);
        cartService.addCart(addCartReqDto, userId);
        Cart newCart = cartRepository.findByProductItemIdAndUserUserId(testItem.getId(), userId);

        //then
        assertThat(cart).isNull();
        assertThat(newCart.getProductItem().getId()).isEqualTo(testItem.getId());
        assertThat(newCart.getQuantity()).isEqualTo(quantity);
    }

    @DisplayName("장바구니에 이미 존재하는 상품을 추가할 때 기존 상품의 수량을 업데이트 한다.")
    @Test
    void shouldUpdateProductItemQuantityInCart() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Cart testCart = createAndSaveCart(2L, testUser, testItem);

        Long quantity = 3L;
        Long totalQuantity = quantity + testCart.getQuantity();
        AddCartReqDto addCartReqDto = new AddCartReqDto(testItem.getId(), quantity);

        //when
        cartService.addCart(addCartReqDto, testUser.getUserId());
        Optional<Cart> updateCart = cartRepository.findById(testCart.getCartId());

        //then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.get().getProductItem().getId()).isEqualTo(testItem.getId());
        assertThat(updateCart.get().getQuantity()).isEqualTo(totalQuantity);
    }


    @DisplayName("존재하지 않는 사용자가 장바구니에 추가하려 하면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenUserNotFoundOnAddCart() {
        // given
        Long userId = 999L;
        AddCartReqDto addCartReqDto = new AddCartReqDto(1L, 2L);

        // when & then
        assertThatThrownBy(() -> cartService.addCart(addCartReqDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("없는 사용자 입니다.");
    }


    @DisplayName("장바구니에 상품을 추가할 때 상품이 존재하지 않으면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenProductItemNotFoundOnAddCart() {
        // given
        Long userId = 1L;
        Long itemId = 999L;
        AddCartReqDto addCartReqDto = new AddCartReqDto(itemId, 2L);

        // when & then
        assertThatThrownBy(() -> cartService.addCart(addCartReqDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 상품의 컬러&사이즈가 존재하지 않습니다.");
    }

    @DisplayName("상품의 재고보다 많은 수량을 장바구니에 추가하려 하면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenProductItemOutOfStock() {
        // given
        Long userId = 1L;
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        AddCartReqDto addCartReqDto = new AddCartReqDto(testItem.getId(), testItem.getQuantity() + 2L);

        // when & then
        assertThatThrownBy(() -> cartService.addCart(addCartReqDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("준비된 수량을 초과했습니다.");
    }

    @DisplayName("준비된 상품의 재고가 0개이면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenProductItemSoldOut() {
        // given
        Long userId = 2L;
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        testItem.updateQuantity(0L); //재고 0개로 변경
        productItemRepository.save(testItem);
        AddCartReqDto addCartReqDto = new AddCartReqDto(testItem.getId(), 3L);

        // when & then
        assertThatThrownBy(() -> cartService.addCart(addCartReqDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("준비된 수량을 초과했습니다.");
    }

    @DisplayName("장바구니 최대 수량(30개)를 초과하면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenCartExceedsMaxLimit() {
        // given
        Long userId = 1L;
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        AddCartReqDto addCartReqDto = new AddCartReqDto(testItem.getId(), 3L);

        // when & then
        assertThatThrownBy(() -> cartService.addCart(addCartReqDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("장바구니 최대 수량을 초과했습니다.");
    }

    @DisplayName("사용자가 장바구니를 조회할 때, 장바구니 정보가 반환된다.")
    @Test
    void shouldReturnCartDetailWhenCartExist() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem firstItem = createAndSaveProductItem("M", testProduct);
        ProductItem secondItem = createAndSaveProductItem("L", testProduct);
        Cart firstCart = createAndSaveCart(3L, testUser, firstItem);
        Cart secondCart = createAndSaveCart(2L, testUser, secondItem);

        // when
        List<CartInfoResDto> response = cartService.getCartDetail(testUser.getUserId());

        // then
        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting(
                        cartInfoResDto -> cartInfoResDto.cartInfoDto().cartId(),
                        cartInfoResDto -> cartInfoResDto.cartInfoDto().itemId(),
                        cartInfoResDto -> cartInfoResDto.cartInfoDto().quantity()
                )
                .contains(
                        tuple(firstCart.getCartId(), firstCart.getProductItem().getId(), firstCart.getQuantity()),
                        tuple(secondCart.getCartId(), secondCart.getProductItem().getId(), secondCart.getQuantity())
                );
    }

    @DisplayName("장바구니가 비어 있으면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenCartIsEmpty() {
        // given
        User testUser = createAndSaveUser();

        // when & then
        assertThatThrownBy(() -> cartService.getCartDetail(testUser.getUserId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("장바구니가 비었습니다.");
    }

    @DisplayName("장바구니 내역을 성공적으로 업데이트 한다.")
    @Test
    void shouldUpdateItemOptionSuccessfully() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        ProductItem secondItem = createAndSaveProductItem("L", testProduct);
        Cart testCart = createAndSaveCart(3L, testUser, testItem);
        Long changeQuantity = 5L;

        UpdateCartReqDto updateCartReqDto = new UpdateCartReqDto(testCart.getCartId(), secondItem.getId(), changeQuantity);

        // when
        cartService.updateItemOption(updateCartReqDto, testUser.getUserId());
        Optional<Cart> updateCart = cartRepository.findById(testCart.getCartId());

        // then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.get().getProductItem().getId()).isEqualTo(secondItem.getId());
        assertThat(updateCart.get().getQuantity()).isEqualTo(changeQuantity);
    }

    @DisplayName("존재하지 않는 사용자가 장바구니 내역 변경을 시도하려 하면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenUserNotFoundOnUpdateCart() {
        //given
        Long userId = 999L;
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Cart testCart = createAndSaveCart(3L, testUser, testItem);
        UpdateCartReqDto updateCartReqDto = new UpdateCartReqDto(testCart.getCartId(), testItem.getId(), 2L);

        // when & then
        assertThatThrownBy(() -> cartService.updateItemOption(updateCartReqDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("없는 사용자 입니다.");
    }

    @DisplayName("장바구니가 존재하지 않으면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenCartNotFound() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Long cartId = 999L; // 존재하지 않는 장바구니 ID
        UpdateCartReqDto updateCartReqDto = new UpdateCartReqDto(cartId, testItem.getId(),3L);

        // when & then
        assertThatThrownBy(() -> cartService.updateItemOption(updateCartReqDto, testUser.getUserId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("요청 하신 내역에 문제가 발생했습니다.");
    }

    @DisplayName("장바구니 내역을 업데이트할 때 상품 아이템이 존재하지 않으면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenProductItemNotFoundOnUpdateCart() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Cart testCart = createAndSaveCart(3L, testUser, testItem);
        Long nonExistentItemId = 999L; // 존재하지 않는 상품 아이템 ID
        Long newQuantity = 3L;
        UpdateCartReqDto updateCartReqDto = new UpdateCartReqDto(testCart.getCartId(), nonExistentItemId, newQuantity);

        // when & then
        assertThatThrownBy(() -> cartService.updateItemOption(updateCartReqDto, testUser.getUserId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 상품의 컬러&사이즈가 존재하지 않습니다.");
    }

    @DisplayName("장바구니 옵션이 변경되지 않으면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenOptionNotChanged() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Long quantity = 3L;
        Cart testCart = createAndSaveCart(quantity, testUser, testItem);
        UpdateCartReqDto updateCartReqDto = new UpdateCartReqDto(testCart.getCartId(), testItem.getId(), quantity);

        // when & then
        assertThatThrownBy(() -> cartService.updateItemOption(updateCartReqDto, testUser.getUserId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("변경된 내역이 없습니다.");
    }

    @DisplayName("재고 수량이 부족하면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWheStockIsNotEnough() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Cart testCart = createAndSaveCart(3L, testUser, testItem);
        Long chageQuantity = 31L;
        UpdateCartReqDto updateCartReqDto = new UpdateCartReqDto(testCart.getCartId(), testItem.getId(), chageQuantity);

        // when & then
        assertThatThrownBy(() -> cartService.updateItemOption(updateCartReqDto, testUser.getUserId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("준비된 수량을 초과했습니다.");
    }

    @DisplayName("장바구니에 담긴 상품 내역을 삭제한다.")
    @Test
    void shouldDeleteCartSussfully() {
        // given
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem1 = createAndSaveProductItem("M", testProduct);
        ProductItem testItem2 = createAndSaveProductItem("L", testProduct);
        Cart testCart1 = createAndSaveCart(3L, testUser, testItem1);
        Cart testCart2 = createAndSaveCart(2L, testUser, testItem2);

        List<Long> cartList = List.of(testCart1.getCartId(), testCart2.getCartId());

        // when
        cartService.deleteCartItem(cartList, testUser.getUserId());

        // then
        assertThat(cartRepository.existsById(testCart1.getCartId())).isFalse();
        assertThat(cartRepository.existsById(testCart2.getCartId())).isFalse();
    }

    @DisplayName("존재하지 않는 사용자가 장바구니 내역 삭제를 시도하려 하면 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenUserNotFoundOnDeleteCart() {
        //given
        Long userId = 999L;
        User testUser = createAndSaveUser();
        Product testProduct = createAndSaveProduct();
        ProductImage testImage = createAndSaveProductImage(testProduct);
        ProductItem testItem = createAndSaveProductItem("M", testProduct);
        Cart testCart = createAndSaveCart(3L, testUser, testItem);

        List<Long> cartList = List.of(testCart.getCartId());

        // when & then
        assertThatThrownBy(() -> cartService.deleteCartItem(cartList, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("없는 사용자 입니다.");
    }

    private User createAndSaveUser() {
        return userRepository.save(User.builder()
                .sex(Sex.FEMALE)
                .nickname("망고")
                .cellPhone("01011112222")
                .birth(LocalDate.of(2000, 1, 2))
                .profileImage("default.jpg")
                .name("김망고")
                .build());
    }

    private Product createAndSaveProduct() {
        return productRepository.save(Product.builder()
                .productName("테스트 상품")
                .brandName("테스트 브랜드")
                .price(50000L)
                .build());
    }

    private ProductItem createAndSaveProductItem(String size, Product product) {
        return productItemRepository.save(ProductItem.builder()
                .color("WHITE")
                .colorCode("#FFFFFF")
                .size(size)
                .quantity(30L)
                .product(product)
                .build());
    }

    private ProductImage createAndSaveProductImage(Product product) {
        return productImageRepository.save(ProductImage.builder()
                .uploadFile(UploadFile.builder()
                        .storedFileName("/images/product/coolfit_bra_volumefit_1.jpg")
                        .uploadFileName("coolfit_bra_volumefit_1.jpg")
                        .build())
                .isRepresentative(true)
                .imageIndex(0L)
                .product(product)
                .build());
    }

    private Cart createAndSaveCart(Long quantity, User user, ProductItem productItem) {
        return cartRepository.save(Cart.builder()
                .quantity(quantity)
                .user(user)
                .productItem(productItem)
                .build());
    }
}