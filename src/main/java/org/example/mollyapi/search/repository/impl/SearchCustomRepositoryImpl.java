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
import org.example.mollyapi.search.dto.AutoWordResDto;
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
        String likeKeyword = "%" + keyword + "%";
        if (keyword != null && !keyword.isEmpty()) {

//            // 대소문자 무시
//            condition.or(product.productName.likeIgnoreCase(likeKeyword))
//                    .or(product.brandName.likeIgnoreCase(likeKeyword))
//                    .or(product.description.likeIgnoreCase(likeKeyword));

            //값이 있을 때만, 검색
            condition.or(product.productName.like(likeKeyword))
                    .or(product.brandName.like(likeKeyword))
                    .or(product.description.like(likeKeyword));

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
                                product.id,
                                productImage.url,
                                product.brandName,
                                product.productName,
                                product.price
                                )
                ).from(product)
                .innerJoin(productImage).on(
                        productImage.product.id.eq(product.id)
                                .and(productImage.isRepresentative.eq(true)))
                .where(condition)
                .limit(pageable.getPageSize())
                ;


        return query.fetch();
    }

    @Override
    public AutoWordResDto searchAutoWord(String keyword) {
        return null;
    }


}
