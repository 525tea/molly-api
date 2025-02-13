package org.example.mollyapi.product.service;
import org.example.mollyapi.product.dto.request.ProductRegisterReqDto;
import org.example.mollyapi.product.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product registerProduct(
            Long userId,
            ProductRegisterReqDto productRegisterReqDto,
            MultipartFile thumbnailImage,
            List<MultipartFile> productImages,
            List<MultipartFile> descriptionImages
            );
    Product updateProduct(Long userId, Long id, ProductRegisterReqDto productRegisterReqDto);
    void deleteProduct(Long userId, Long id);
}
