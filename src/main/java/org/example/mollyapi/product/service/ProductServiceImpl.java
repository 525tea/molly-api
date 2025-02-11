package org.example.mollyapi.product.service;

import org.example.mollyapi.product.dto.UploadFile;
import org.example.mollyapi.product.dto.request.ProductRegisterReqDto;
import org.example.mollyapi.product.entity.Category;
import org.example.mollyapi.product.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    @Override
    public Product updateProduct(Long id, ProductRegisterReqDto productRegisterReqDto) {
        return Product.builder()
                .category(new Category("category1", null))
                .brandName(productRegisterReqDto.brandName() + "-hwan")
                .productName(productRegisterReqDto.productName())
                .price(productRegisterReqDto.price())
                .description(productRegisterReqDto.description())
                .thumbnail(new UploadFile("uploadedFile", "storedFile"))
                .productImages(List.of(new UploadFile("uploadedFile", "storedFile")))
                .descriptionImages(List.of(new UploadFile("uploadedFile", "storedFile")))
                .items(new ArrayList<>())
                .build();
    }

    @Override
    public Product registerProduct(
            ProductRegisterReqDto productRegisterReqDto,
            MultipartFile thumbnailImage,
            List<MultipartFile> productImages,
            List<MultipartFile> descriptionImages
    ) {
        
        return Product.builder()
                .category(new Category("category1", null))
                .brandName(productRegisterReqDto.brandName() + "-hwan")
                .productName(productRegisterReqDto.productName())
                .price(productRegisterReqDto.price())
                .description(productRegisterReqDto.description())
                .thumbnail(new UploadFile("uploadedFile", "storedFile"))
                .productImages(List.of(new UploadFile("uploadedFile", "storedFile")))
                .descriptionImages(List.of(new UploadFile("uploadedFile", "storedFile")))
                .items(new ArrayList<>())
                .build();
    }

    @Override
    public Optional<Product> getProductById(Long id) {

        return Optional.of(Product.builder()
                .category(new Category("category1", null))
                .brandName("brandName")
                .productName("productName")
                .price(20000L)
                .description("description")
                .thumbnail(new UploadFile("uploadedFile", "storedFile"))
                .productImages(List.of(new UploadFile("uploadedFile", "storedFile")))
                .descriptionImages(List.of(new UploadFile("uploadedFile", "storedFile")))
                .items(new ArrayList<>())
                .build());
    }

    @Override
    public void deleteProduct(Long id) {

    }
}
