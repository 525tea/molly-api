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

        public void decreaseStock(int quantityToDecrease) {
                if (this.quantity < quantityToDecrease) {
                        throw new IllegalArgumentException("재고 부족: 현재 수량=" + this.quantity + ", 요청 수량=" + quantityToDecrease);
                }
                this.quantity -= quantityToDecrease;
        }
}
