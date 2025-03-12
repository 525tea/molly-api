package org.example.mollyapi.product.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.product.dto.request.BulkProductItemReqDto;
import org.example.mollyapi.product.dto.request.BulkProductReqDto;
import org.example.mollyapi.product.entity.Category;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductItem;
import org.example.mollyapi.product.repository.CategoryRepository;
import org.example.mollyapi.product.repository.ProductItemRepository;
import org.example.mollyapi.product.repository.ProductRepository;
import org.example.mollyapi.product.service.BulkProductService;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import static org.example.mollyapi.common.exception.error.impl.CategoryError.INVALID_CATEGORY;
import static org.example.mollyapi.common.exception.error.impl.ProductItemError.PROBLEM_REGISTERING_BULK_PRODUCTS;
import static org.example.mollyapi.common.exception.error.impl.UserError.NOT_EXISTS_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkProductServiceImpl implements BulkProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final UserRepository userRepository;

    private static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    @Transactional
    public List<Map<String, String>> saveBulkProducts(MultipartFile file, Long userId) {

        List<Map<String, String>> invalidProduct = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        try (InputStream inputStream = file.getInputStream()) {

            int rowIndex = 1;
            List<BulkProductReqDto> passedProduct = new ArrayList<>();
            List<BulkProductItemReqDto> passedProductItem = new ArrayList<>();

            Workbook wb = new XSSFWorkbook(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {

                boolean existProduct = false;
                BulkProductItemReqDto bulkProductItemReqDto = null;

                Row row = rowIterator.next();

                String productName = getCellValue(row.getCell(0));
                String description = getCellValue(row.getCell(1));
                long categoryId = Long.parseLong(getCellValue(row.getCell(2)));
                String brandName = getCellValue(row.getCell(3));
                long price = Long.parseLong(getCellValue(row.getCell(4)));
                String color = getCellValue(row.getCell(5));
                String colorCode = getCellValue(row.getCell(6));
                long quantity = Long.parseLong(getCellValue(row.getCell(7)));
                String size = getCellValue(row.getCell(8));


                Map<String, String> errorMap = validProduct(productName,
                        description,
                        quantity,
                        brandName,
                        color,
                        colorCode,
                        price,
                        size,
                        rowIndex);

                if (!errorMap.isEmpty()){
                    invalidProduct.add(errorMap);
                    rowIndex++;
                    continue;
                }



                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(INVALID_CATEGORY));


                for (BulkProductReqDto bulkProductReqDto : passedProduct) {

                    if (bulkProductReqDto.getProductName().equals(productName)) {

                        bulkProductItemReqDto = createBulkProductItemReqDto(bulkProductReqDto,
                                color,
                                colorCode,
                                quantity,
                                size);

                        existProduct = true;
                        break;
                    }
                }

                if (!existProduct) {

                    BulkProductReqDto bulkProductReqDto = BulkProductReqDto.builder()
                            .productName(productName)
                            .description(description)
                            .category(category)
                            .brandName(brandName)
                            .price(price)
                            .build();

                    bulkProductItemReqDto = createBulkProductItemReqDto(bulkProductReqDto,
                            color,
                            colorCode,
                            quantity,
                            size);

                    passedProduct.add(bulkProductReqDto);
                }
                passedProductItem.add(bulkProductItemReqDto);
                rowIndex++;
            }

            List<Product> products = passedProduct.stream()
                    .map(bulkProductReqDto -> bulkProductReqDto.toProduct(user)
                    ).toList();

            productRepository.saveAll(products);

            List<ProductItem> productItems = passedProductItem.stream()
                    .map(bulkProductItemReqDto -> {
                        Product product = productRepository.getReferenceById(bulkProductItemReqDto.getProductId());
                        return bulkProductItemReqDto.toProductItem(product);
                    }).toList();

            productItemRepository.saveAll(productItems);

        } catch (IOException e) {
            throw new CustomException(PROBLEM_REGISTERING_BULK_PRODUCTS);
        }

        return invalidProduct;
    }

    private BulkProductItemReqDto createBulkProductItemReqDto(BulkProductReqDto bulkProductReqDto,
                                                              String color,
                                                              String colorCode,
                                                              long quantity,
                                                              String size) {
        return BulkProductItemReqDto.builder()
                .productId(bulkProductReqDto.getId())
                .color(color)
                .colorCode(colorCode)
                .quantity(quantity)
                .size(size)
                .build();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // 정수 변환
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf((long) cell.getNumericCellValue()); // 숫자 변환
                } catch (IllegalStateException e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }

    private Map<String, String> validProduct(
            String productName,
            String description,
            long quantity,
            String brandName,
            String color,
            String colorCode,
            long price,
            String size,
            int rowIndex
    ) {
        Map<String, String> rowErrorMap = new HashMap<>();


        if (productName == null || productName.isBlank()) {
            rowErrorMap.put("상품명","유효하지 않은 상품명");
        }

        if (description == null || description.length() < 10) {
            rowErrorMap.put("상품설명" ,"설명은 최소 10글자 이상");
        }

        if (quantity <= 0) {
            rowErrorMap.put("수량","수량은 1 이상이어야 함");
        }

        if (brandName == null || brandName.isBlank()) {
            rowErrorMap.put("브랜드명","유효하지 않은 브랜드명");
        }

        if (color == null || color.isBlank()) {
            rowErrorMap.put("색상","유효하지 않은 색입니다.");
        }

        if (colorCode == null || colorCode.isBlank() || !HEX_PATTERN.matcher(colorCode).matches()) {
            rowErrorMap.put("색상코드","유효하지 않은 색 코드입니다.");
        }

        if (size == null || size.isBlank()) {
            rowErrorMap.put("사이즈","유효하지 않은 사이즈 입니다.");
        }

        if (price < 0) {
            rowErrorMap.put("가격","가격이 0 이상이어야 합니다.");
        }

        if(!rowErrorMap.isEmpty()){
            rowErrorMap.put("행", String.valueOf(rowIndex));
        }
        return rowErrorMap;
    }


}
