package org.example.mollyapi.review.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.mollyapi.review.dto.response.MyReviewInfoDto;
import org.example.mollyapi.review.dto.response.ReviewInfoDto;
import org.example.mollyapi.review.repository.ReviewCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.example.mollyapi.product.entity.QProductImage.productImage;
import static org.example.mollyapi.user.entity.QUser.user;
import static org.example.mollyapi.review.entity.QReview.review;
import static org.example.mollyapi.review.entity.QReviewLike.reviewLike;
import static org.example.mollyapi.review.entity.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<ReviewInfoDto> getReviewInfo(Pageable pageable, Long productId, Long userId) {
         List<ReviewInfoDto> infoList = jpaQueryFactory.select(
                Projections.constructor(ReviewInfoDto.class,
                        review.id,
                        review.content,
                        user.nickname,
                        user.profileImage,
                        reviewLike.isLike.coalesce(Boolean.FALSE).as("isLike"),
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", review.createdAt)
                )).from(review)
                .innerJoin(review.user, user)
                .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id)
                        .and(reviewLike.user.userId.eq(userId)))
                .where(review.product.id.eq(productId)
                        .and(review.isDeleted.eq(Boolean.FALSE)))
                .orderBy(review.createdAt.desc())
                .fetch();

         boolean hasNext = false;
         if (infoList.size() > pageable.getPageSize()) {
             infoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<ReviewInfoDto>(infoList, pageable, hasNext);
    }

    @Override
    public List<String> getImageList(Long reviewId) {
        return jpaQueryFactory.select(
                    reviewImage.url
                ).from(reviewImage)
                .where(reviewImage.review.id.eq(reviewId))
                .fetch();
    }

    @Override
    public Slice<MyReviewInfoDto> getMyReviewInfo(Pageable pageable, Long userId) {
        List<MyReviewInfoDto> infoList = jpaQueryFactory.select(
                Projections.constructor(MyReviewInfoDto.class,
                        review.id,
                        review.content,
                        review.product.id,
                        review.product.productName.coalesce("게시 중단된 상품입니다."),
                        productImage.url.coalesce("게시 중단된 상품입니다."),
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", review.createdAt)
                )).from(review)
                .innerJoin(productImage).on(review.product.id.eq(productImage.product.id)
                        .and(productImage.isRepresentative.eq(Boolean.TRUE)))
                .where(review.isDeleted.eq(Boolean.FALSE)
                        .and(review.user.userId.eq(userId)))
                .orderBy(review.createdAt.desc())
                .fetch();

        boolean hasNext = false;
        if (infoList.size() > pageable.getPageSize()) {
            infoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<MyReviewInfoDto>(infoList, pageable, hasNext);

    }
}
