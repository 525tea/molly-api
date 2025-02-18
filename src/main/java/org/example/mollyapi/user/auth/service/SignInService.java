package org.example.mollyapi.user.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.user.auth.config.Jwt;
import org.example.mollyapi.user.auth.config.PasswordEncoder;
import org.example.mollyapi.user.auth.dto.SignInReqDto;
import org.example.mollyapi.user.auth.dto.SignInResDto;
import org.example.mollyapi.user.auth.entity.Auth;
import org.example.mollyapi.user.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;



import static org.example.mollyapi.common.exception.error.impl.AuthError.*;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwt jwt;

    /***
     * 로그인
     * @param signInReqDto 로그인 시도 요청 Info
     * @return JWT
     */
    public SignInResDto signIn(SignInReqDto signInReqDto){

        Auth auth = authRepository.findByEmail(signInReqDto.email())
                .orElseThrow(() -> new CustomException(NOT_MATCH_AUTH));

        if(!passwordEncoder.check(auth, signInReqDto.password())){
            throw new CustomException(NOT_MATCH_AUTH);
        }

        auth.updatedLastLoginAt();
        authRepository.save(auth);


        return new SignInResDto(
                jwt.generateToken(auth.getAuthId(),
                        auth.getUser().getUserId(),
                        auth.getEmail(),
                        auth.getRole()),
                auth.getRole()
        );
    }
}
