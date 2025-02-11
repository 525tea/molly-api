package org.example.mollyapi.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mollyapi.product.dto.UploadFile;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    Long id;
    String url;
    String filename;
    Boolean isProductImage;
    Boolean isRepresentative;
    Boolean isDescriptionImage;
    Long imageIndex;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Builder
    ProductImage(UploadFile uploadFile,
                 Boolean isProductImage,
                 Boolean isRepresentative,
                 Boolean isDescriptionImage,
                 Long imageIndex) {
        this.url = uploadFile.getStoredFileName();
        this.url = uploadFile.getUploadFileName();
        this.isProductImage = isProductImage;
        this.isRepresentative = isRepresentative;
        this.isDescriptionImage = isDescriptionImage;
        this.imageIndex = imageIndex;
    }
}
