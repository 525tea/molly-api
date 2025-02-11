package org.example.mollyapi.product.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductItemReqDto(
        @NotBlank String color,
        @NotBlank String colorCode,
        @NotBlank String size,
        @NotBlank Long quantity
) {}
