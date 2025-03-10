package org.example.mollyapi.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.mollyapi.review.dto.request.AddReviewReqDto;
import org.example.mollyapi.review.dto.response.GetMyReviewResDto;
import org.example.mollyapi.review.dto.response.GetReviewResDto;
import org.example.mollyapi.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Mock
    private HttpServletRequest mockRequest;

    @DisplayName("새로운 리뷰를 등록한다.")
    @Test
    void registerReview() throws Exception {
        // given
        Long userId = 1L;
        AddReviewReqDto addReviewReqDto = new AddReviewReqDto(1L, "Test Content");
        MockMultipartFile reviewFile = new MockMultipartFile(
                "review", "review.json", "application/json", new ObjectMapper().writeValueAsBytes(addReviewReqDto)
        );
        MockMultipartFile reviewImagesFile = new MockMultipartFile(
                "reviewImages", "review_images.jpg", "image/jpeg", new byte[0]
        );

        when(mockRequest.getAttribute("userId")).thenReturn(userId);

        // when & then
        mockMvc.perform(
                multipart("/review")
                        .file(reviewFile)
                        .file(reviewImagesFile)
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 등록에 성공했습니다."));
    }

    @DisplayName("회원용 상품별 리뷰 내역을 조회한다.")
    @Test
    void getReviewList() throws Exception {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int page = 0;
        int size = 10;

        SliceImpl<GetReviewResDto> sliceList = new SliceImpl<>(new ArrayList<>());
        when(reviewService.getReviewList(any(PageRequest.class), eq(productId), anyLong()))
                .thenReturn(sliceList);
        when(mockRequest.getAttribute("userId")).thenReturn(userId);

        // when & then
        mockMvc.perform(
                get("/review/{productId}", productId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .requestAttr("userId", userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("비회원용 상품별 리뷰 내역을 조회한다.")
    @Test
    void getReviewListByNonUser() throws Exception {
        // given
        Long productId = 1L;
        int page = 0;
        int size = 10;

        SliceImpl<GetReviewResDto> sliceList = new SliceImpl<>(new ArrayList<>());
        when(reviewService.getReviewList(any(PageRequest.class), eq(productId), eq(0L)))
                .thenReturn(sliceList);

        // when & then
        mockMvc.perform(
                get("/review/{productId}/new", productId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 자신의 리뷰 내역을 조회한다.")
    @Test
    void getMyReviewList() throws Exception {
        // given
        Long userId = 1L;
        int page = 0;
        int size = 10;

        SliceImpl<GetMyReviewResDto> sliceList = new SliceImpl<>(new ArrayList<>());
        when(reviewService.getMyReviewList(any(PageRequest.class), eq(userId))).thenReturn(sliceList);
        when(mockRequest.getAttribute("userId")).thenReturn(userId);

        // when & then
        mockMvc.perform(
                get("/review/myReview")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .requestAttr("userId", userId))
                .andExpect(status().isOk());
    }

    @DisplayName("자신이 작성한 리뷰 내역을 수정한다.")
    @Test
    void updateReview() throws Exception {
        // given
        Long userId = 1L;
        AddReviewReqDto addReviewReqDto = new AddReviewReqDto(1L, "Updated Content");
        MockMultipartFile reviewFile = new MockMultipartFile(
                "review", "review.json", "application/json", new ObjectMapper().writeValueAsBytes(addReviewReqDto)
        );
        MockMultipartFile reviewImagesFile = new MockMultipartFile(
                "updateImage", "update_image.jpg", "image/jpeg", new byte[0]
        );

        // when & then
        mockMvc.perform(
                multipart("/review")
                        .file(reviewFile)
                        .file(reviewImagesFile)
                        .with(mockHttpServletRequest -> {
                            mockHttpServletRequest.setAttribute("userId", userId);
                            mockHttpServletRequest.setMethod("PUT");
                            return mockHttpServletRequest;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 수정에 성공했습니다."));
    }

    @DisplayName("자신이 작성한 리뷰를 삭제한다.")
    @Test
    void deleteReview() throws Exception {
        // given
        Long userId = 1L;
        Long reviewId = 1L;

        when(mockRequest.getAttribute("userId")).thenReturn(userId);

        // when & then
        mockMvc.perform(
                delete("/review/{reviewId}", reviewId)
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 삭제에 성공했습니다."));
    }
}
