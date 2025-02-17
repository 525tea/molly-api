package org.example.mollyapi.review.repository;

import org.example.mollyapi.review.dto.response.ReviewInfo;

import java.util.List;
import java.util.Optional;

public interface ReviewCustomRepository {
    List<ReviewInfo> getReviewInfo(Long productId, Long userId);
    List<String> getImageList(Long reviewId);
}
