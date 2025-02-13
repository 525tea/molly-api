package org.example.mollyapi.search.repository.impl;

import com.querydsl.core.BooleanBuilder;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.search.dto.SearchItemResDto;
import org.example.mollyapi.search.repository.SearchCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.time.LocalDateTime;
import java.util.List;

import static org.example.mollyapi.product.entity.QProduct.product;
import static org.example.mollyapi.product.entity.QProductImage.*;


@RequiredArgsConstructor
public class SearchCustomRepositoryImpl implements SearchCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SearchItemResDto> search(String keyword,
                                         Long cursorId,
                                         LocalDateTime lastCreatedAt,
                                         Pageable pageable) {

        BooleanBuilder condition = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {

            //대소문자 무시
            condition.and(product.productName.likeIgnoreCase(keyword))
                    .or(product.brandName.likeIgnoreCase(keyword))
                    .or(product.description.likeIgnoreCase(keyword));

            //값이 있을 때만, 검색
            condition.and(product.productName.like("%" + keyword + "%"))
                    .or(product.brandName.like("%" + keyword + "%"))
                    .or(product.description.like("%" + keyword + "%"));

        }

        // 값 중복을 피하기 위한 조건 값
        if (lastCreatedAt != null && cursorId != null) {

            condition.and(product.createdAt.lt(lastCreatedAt))
                    .or(
                            product.createdAt.eq(lastCreatedAt)
                                    .and(product.id.lt(cursorId)));
        }

        JPAQuery<SearchItemResDto> query = jpaQueryFactory
                .select(
                        Projections.constructor(SearchItemResDto.class,
                                product.id.as("productId"),
                                productImage.url,
                                product.brandName,
                                product.productName,
                                product.price
                                )
                ).from(product)
                .innerJoin(productImage).on(
                        productImage.product.id.eq(product.id)
                                .and(productImage.isProductImage.eq(true)))
                .where(condition)
                .limit(pageable.getPageSize())
                ;

        return query.fetch();
    }

}
