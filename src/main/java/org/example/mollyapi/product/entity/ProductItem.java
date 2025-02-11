package org.example.mollyapi.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "item_id")
        Long id;

        String color;
        String colorCode;
        String size;
        Long quantity;

        @ManyToOne
        @JoinColumn(name = "product_id")
        Product product;

        @Builder
        ProductItem(
                String color,
                String colorCode,
                String size,
                Long quantity,
                Product product) {
                this.color = color;
                this.colorCode = colorCode;
                this.size = size;
                this.quantity = quantity;
                this.product = product;
        }
}
