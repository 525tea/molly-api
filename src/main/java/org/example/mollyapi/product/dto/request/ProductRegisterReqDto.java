package org.example.mollyapi.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.mollyapi.product.dto.UploadFile;
import org.example.mollyapi.product.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductRegisterReqDto(
            Long id,

            @NotNull(message = "카테고리는 필수입니다")
            List<@NotBlank(message = "카테고리명은 공백일 수 없습니다") String> categories,

            @NotBlank(message = "브랜드명은 필수입니다")
            String brandName,

            @NotBlank
            String productName,

            @NotNull
            @Min(value = 0, message = "가격은 0 이상의 숫자여야 합니다.") // 음수 방지
            Long price,

            @NotNull String description,

            @NotNull(message = "상품은 한 개 이상 등록해야합니다")
            List<ProductItemReqDto> items // [[ 색상, 색상코드, 사이즈 ], ...]
            ) {

};
