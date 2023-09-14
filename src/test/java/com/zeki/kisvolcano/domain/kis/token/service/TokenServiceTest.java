package com.zeki.kisvolcano.domain.kis.token.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
class TokenServiceTest {

    @Autowired
    TokenService tokenService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Transactional
    void testCheckGetToken_NotExpired() {
        // case

        // when
        String tokenValue = tokenService.checkGetToken();
        String tokenValue2 = tokenService.checkGetToken();

        // then
        assertEquals(tokenValue, tokenValue2);
    }

    @Test
    void testCheckGetToken_Expired() {

    }
}