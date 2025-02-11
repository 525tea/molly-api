package org.example.mollyapi.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.common.exception.error.impl.UserError;
import org.example.mollyapi.user.dto.*;
import org.example.mollyapi.user.entity.User;
import org.example.mollyapi.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.example.mollyapi.common.exception.error.impl.UserError.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /***
     * 사용자 정보 조회1 - 마이페이지 프로필 수정 기능에 필요한 조회 데이터
     * @param authId 인증 PK
     * @return GetUserInfoResDto
     */
    public GetUserInfoResDto getUserInfo(Long authId) {
        validUser(authId);

        return userRepository.getUserInfo(authId);
    }


    /***
     * 사용자 이메일, 포인트, 이름 조회
     * @param authId 인증 PK
     * @return GetUserSummaryInfoWithPointResDto
     */
    public GetUserSummaryInfoWithPointResDto getUserSummaryWithPoint(Long authId) {
        validUser(authId);

        return userRepository.getUserSummaryInfo(authId);
    }

    public GetUserSummaryInfoResDto getUserSummaryInfo(Long authId) {
        validUser(authId);

        GetUserSummaryInfoWithPointResDto userSummaryInfo
                = userRepository.getUserSummaryInfo(authId);
        return new GetUserSummaryInfoResDto(userSummaryInfo.name(), userSummaryInfo.email());
    }

    /***
     * 사용자 유효성 검증
     * @param authId 인증 PK
     */
    private void validUser(Long authId) {
        boolean exists = userRepository.existsByAuth_AuthId(authId);

        if (!exists) throw new CustomException(NOT_EXISTS_USER);
    }

    @Transactional
    public ResponseEntity<?> updateUserInfo(UpdateUserReqDto updateUserReqDto, Long authId) {
        User user = userRepository.findByAuth_AuthId(authId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        boolean isUpdate = user.updateUser(updateUserReqDto);

        UpdateUserResDto updateUserResDto = new UpdateUserResDto(
                user.getName(),
                user.getNickname(),
                user.getCellPhone(),
                user.getSex(),
                user.getBirth()
        );

        if (isUpdate) {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(updateUserResDto);
        }

        return ResponseEntity.status(HttpStatusCode.valueOf(204)).build();
    }
}
