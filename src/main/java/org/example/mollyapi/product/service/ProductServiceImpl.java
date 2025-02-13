package org.example.mollyapi.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mollyapi.product.dto.UploadFile;
import org.example.mollyapi.product.dto.request.ProductItemReqDto;
import org.example.mollyapi.product.dto.request.ProductRegisterReqDto;
import org.example.mollyapi.product.entity.Category;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductImage;
import org.example.mollyapi.product.entity.ProductItem;
import org.example.mollyapi.product.file.FileStore;
import org.example.mollyapi.product.repository.ProductRepository;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryService categoryService;
    private final FileStore fileStore;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public Product updateProduct(Long userId, Long id, ProductRegisterReqDto productRegisterReqDto) {

        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);

        // 업데이트 할 상품 조회
        Product product = productRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        // 유저-상품 검증
        if (!userId.equals(product.getUser().getUserId())) {
            throw new IllegalArgumentException();
        }

        List<ProductItem> productItems = productRegisterReqDto.items().stream().map(ProductItemReqDto::toEntity).toList();
        
        return product.update(
                categoryService.getCategory(productRegisterReqDto.categories()),
                productRegisterReqDto.brandName(),
                productRegisterReqDto.productName(),
                productRegisterReqDto.price(),
                productRegisterReqDto.description(),
                productItems,
                new ArrayList<>()
        );
    }

    @Override
    @Transactional
    public Product registerProduct(
            Long userId,
            ProductRegisterReqDto productRegisterReqDto,
            MultipartFile thumbnailImage,
            List<MultipartFile> productImages,
            List<MultipartFile> descriptionImages
    ) {

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);

        // 카테고리 검색
        Category category = categoryService.getCategory(productRegisterReqDto.categories());

        // 파일 저장
        UploadFile uploadThumbnail = fileStore.storeFile(thumbnailImage);
        List<UploadFile> uploadProductImages = fileStore.storeFiles(productImages);
        List<UploadFile> uploadDescriptionImages = fileStore.storeFiles(descriptionImages);

        // 저장된 파일로 상품 이미지 생성
        List<ProductImage> images = new ArrayList<>();
        images.add(ProductImage.createThumbnail(null, uploadThumbnail));
        for (int i = 0; i < uploadProductImages.size(); i++) {
            images.add(ProductImage.createProductImage(null, uploadProductImages.get(i), i+1));
        }

        for (int i = 0; i < uploadDescriptionImages.size(); i++) {
            images.add(ProductImage.createDescriptionImage(null, uploadDescriptionImages.get(i), i));
        }

        // 아이템 생성
        List<ProductItem> items = productRegisterReqDto.items().stream().map(ProductItemReqDto::toEntity).toList();

        Product newProduct = Product.builder()
                .category(category)
                .brandName(productRegisterReqDto.brandName())
                .productName(productRegisterReqDto.productName())
                .price(productRegisterReqDto.price())
                .description(productRegisterReqDto.description())
                .images(images)
                .items(items)
                .user(user)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    @Transactional
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long userId,Long id) {
        Product product = productRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if (product.getUser().getUserId().equals(userId)) {
            productRepository.deleteById(id);
        }
    }
}
