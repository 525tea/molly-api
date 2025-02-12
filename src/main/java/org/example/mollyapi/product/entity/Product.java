package org.example.mollyapi.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mollyapi.product.dto.UploadFile;
import org.example.mollyapi.user.entity.User;


import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    String brandName;
    String productName;
    Long price;
    String description;

    @OneToMany(mappedBy = "product")
    List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    List<ProductItem> items;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    @Builder
    public Product(
            Category category,
            String brandName,
            String productName,
            Long price,
            String description,
            UploadFile thumbnail,
            List<UploadFile> productImages,
            List<UploadFile> descriptionImages,
            List<ProductItem> items,
            User user
    ) {
        ArrayList<ProductImage> images = createImages(thumbnail, productImages, descriptionImages);

        this.category = category;
        this.brandName = brandName;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.items = items;
        this.images = images;
        this.user = user;
    }

    public UploadFile getThumbnail() {
        ProductImage productImage = images.stream()
                .filter(img -> img.isRepresentative)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No product image found"));

        return new UploadFile(productImage.url, productImage.filename);
    }

    public List<UploadFile> getProductImages() {
        return images.stream()
                .filter(img -> img.isProductImage)
                .map(img -> new UploadFile(img.url, img.filename))
                .toList();
    }

    public List<UploadFile> getDescriptionImages() {
        return images.stream()
                .filter(img -> img.isDescriptionImage)
                .map(img -> new UploadFile(img.url, img.filename))
                .toList();
    }

    private static ArrayList<ProductImage> createImages(UploadFile thumbnail, List<UploadFile> productImages, List<UploadFile> descriptionImages) {
        ArrayList<ProductImage> images = new ArrayList<>();
        images.add(createProductImage(thumbnail, false, true, false, 0));

        for (int idx = 0; idx < productImages.size(); idx++) {
            UploadFile img = productImages.get(idx);
            ProductImage productImage = createProductImage(img, true, false, false, idx + 1);
            images.add(productImage);  // 생성된 ProductImage 객체를 리스트에 추가
        }

        for (int idx = 0; idx < productImages.size(); idx++) {
            UploadFile img = descriptionImages.get(idx);
            ProductImage descriptionImage = createProductImage(img, false, false, true, idx);
            images.add(descriptionImage);  // 생성된 ProductImage 객체를 리스트에 추가
        }
        return images;
    }

    private static ProductImage createProductImage(
            UploadFile thumbnail,
            boolean isProductImage,
            boolean isRepresentative,
            boolean isDescriptionImage,
            long idx
    ) {
        return ProductImage.builder()
                .uploadFile(thumbnail)
                .isProductImage(isProductImage)
                .isRepresentative(isRepresentative)
                .isDescriptionImage(isDescriptionImage)
                .imageIndex(idx)
                .build();
    }
}
