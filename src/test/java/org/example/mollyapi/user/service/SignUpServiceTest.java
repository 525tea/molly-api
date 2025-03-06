package org.example.mollyapi.user.service;

import org.example.mollyapi.user.auth.repository.AuthRepository;
import org.example.mollyapi.user.dto.SignUpReqDto;
import org.example.mollyapi.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SignUpServiceTest {

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;

    @Test
    @DisplayName("필수 값을 입력하면, 회원가입을 할 수 있다.")
    void signUp() {

    }
}