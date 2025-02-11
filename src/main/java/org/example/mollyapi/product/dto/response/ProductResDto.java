package org.example.mollyapi.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResDto(
        Long id,
        List<String> categories,
        String brandName,
        String productName,
        Long price,
        String description,
        FileInfo thumbnail,
        List<FileInfo> productImages,
        List<FileInfo> productDescriptionImages,
        List<ProductItemResDto> items,
        List<ColorDetail> colorDetails
) {

    @Data
    @AllArgsConstructor
    static public class SizeDetail{
        Long id;
        String size;
        Long quantity;
    }

    @Data
    @AllArgsConstructor
    static public class ColorDetail {
        String color;
        String colorCode;
        List<SizeDetail> sizeDetails;
    }

    @Data
    @AllArgsConstructor
    static public class FileInfo {
        String path;
        String filename;
    }

    static List<ColorDetail> groupItemByColor(List<ProductItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getColor() + "_" + item.getColorCode(), // 그룹화 키 생성
                        LinkedHashMap::new, // 순서를 유지하는 맵 사용
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(groupedItems -> new ColorDetail(
                        groupedItems.get(0).getColor(),
                        groupedItems.get(0).getColorCode(),
                        groupedItems.stream()
                                .map(item -> new SizeDetail(item.getId(), item.getSize(), item.getQuantity()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    static public ProductResDto from(Product product) {
        FileInfo thumbnail = new FileInfo(product.getThumbnail().getStoredFileName(), product.getThumbnail().getUploadFileName());
        List<FileInfo> productImages = product.getProductImages().stream().map((item)-> new FileInfo(item.getStoredFileName(), item.getUploadFileName())).toList();
        List<FileInfo> descriptionImages = product.getDescriptionImages().stream().map(item -> new FileInfo(item.getStoredFileName(), item.getUploadFileName())).toList();

        List<ProductItemResDto> itemResDtos = product.getItems().stream().map(ProductItemResDto::from).toList();
        List<ColorDetail> colorDetails = groupItemByColor(product.getItems());

        List<String> categories = new ArrayList<>();

        return new ProductResDto(
                product.getId(),
                categories,
                product.getBrandName(),
                product.getProductName(),
                product.getPrice(),
                product.getDescription(),
                thumbnail,
                productImages,
                descriptionImages,
                itemResDtos,
                colorDetails
        );
    }
}
