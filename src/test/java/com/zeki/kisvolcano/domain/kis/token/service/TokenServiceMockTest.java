package com.zeki.kisvolcano.domain.kis.token.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeki.kisvolcano.config.PropertiesMapping;
import com.zeki.kisvolcano.domain._common.em.TradeMode;
import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain._common.web_client.statics.ApiStatics;
import com.zeki.kisvolcano.domain.kis.token.entity.Token;
import com.zeki.kisvolcano.domain.kis.token.repository.TokenRepository;
import com.zeki.kisvolcano.exception.APIException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceMockTest {
    @InjectMocks
    TokenService tokenService;

    @Mock
    TokenRepository tokenRepository;
    @Mock
    WebClientConnector webClientConnector;
    @Mock
    ApiStatics apiStatics;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    PropertiesMapping pm;

    @Mock
    WebClientConnector.ConnectKisApiBuilder<Map<String, String>, String> connectKisApiBuilder;

    LocalDateTime expiredDate;
    Token notExpToken;
    Token expToken;

    @BeforeEach
    void setUp() {
        expiredDate = LocalDateTime.now().plusDays(1);

        notExpToken = Token.builder()
                .tokenValue("Bearer not_exp_token")
                .mode(TradeMode.TRAIN)
                .expiredDate(expiredDate)
                .build();

        expToken = Token.builder()
                .tokenValue("Bearer exp_token")
                .mode(TradeMode.TRAIN)
                .expiredDate(expiredDate.minusDays(1))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("만료되지 않은 토큰")
        void testCheckGetToken_NotExpired() throws NoSuchFieldException, IllegalAccessException {
            // case when
            Field field = TokenService.class.getDeclaredField("token");
            field.setAccessible(true);
            field.set(tokenService, notExpToken);
            String s = tokenService.checkGetToken();

            // then
            assertEquals(s, notExpToken.getTokenValue());
        }

        @Test
        @DisplayName("만료된 토큰")
        void testCheckGetToken_Expired() throws IllegalAccessException, NoSuchFieldException, JsonProcessingException, NoSuchMethodException, InvocationTargetException {
            // case
            ObjectMapper realObjectMapper = new ObjectMapper();
            String response = "{\n" +
                              "    \"access_token\" : \"not_exp_token\",\n" +
                              "    \"access_token_token_expired\" : \"" + notExpToken.getExpiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"\n" +
                              "}";

            JsonNode value = realObjectMapper.readTree(response);
            when(objectMapper.readTree(response))
                    .thenReturn(value);

            when(connectKisApiBuilder.build())
                    .thenReturn(ResponseEntity.ok(response));

            ApiStatics.Kis kis = new ApiStatics.Kis();
            kis.setAppKey("key");
            kis.setAppSecret("secret");
            when(apiStatics.getKis())
                    .thenReturn(kis);

            when(pm.getMode())
                    .thenReturn(TradeMode.TRAIN);

            when(webClientConnector.<Map<String, String>, String>connectKisApiBuilder())
                    .thenReturn(connectKisApiBuilder);

            when(connectKisApiBuilder.method(HttpMethod.POST)).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.path("/oauth2/tokenP")).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.requestHeaders(null)).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.requestParams(null)).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.requestBody(any(Map.class))).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.classType(String.class)).thenReturn(connectKisApiBuilder);

            when(tokenRepository.findAll())
                    .thenReturn(List.of(expToken));
            when(tokenRepository.save(any(Token.class)))
                    .thenAnswer(invocation -> invocation.<Token>getArgument(0));
            // when
            Field field = TokenService.class.getDeclaredField("token");
            field.setAccessible(true);
            field.set(tokenService, expToken);
            String s1 = tokenService.checkGetToken();

            when(tokenRepository.findAll())
                    .thenReturn(List.of(expToken, notExpToken));
            Field field1 = TokenService.class.getDeclaredField("token");
            field1.setAccessible(true);
            field1.set(tokenService, expToken);
            String s2 = tokenService.checkGetToken();

            Method method = TokenService.class.getDeclaredMethod("init");
            method.setAccessible(true);
            method.invoke(tokenService);
            String s3 = tokenService.checkGetToken();

            // then
            assertEquals(s1, notExpToken.getTokenValue());
            assertEquals(s1, s2);
            assertEquals(s1, s3);
        }

    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {
        @Test
        @DisplayName("Json 파싱 실패")
        void testCheckGetToken_Json() throws JsonProcessingException, NoSuchFieldException, IllegalAccessException {
            // case
            ObjectMapper realObjectMapper = new ObjectMapper();
            String goodResponse = "{\n" +
                                  "    \"access_token\" : \"not_exp_token\",\n" +
                                  "    \"access_token_token_expired\" : \"" + notExpToken.getExpiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"\n" +
                                  "}";

            String badResponse = "{\n" +
                                 "    \"access_token\" : \"not_exp_token\",\n" +
                                 "    \"access_token_token_expired\" : \"" + notExpToken.getExpiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"\n";

            JsonNode value = realObjectMapper.readTree(goodResponse);
            when(objectMapper.readTree(badResponse))
                    .thenThrow(JsonProcessingException.class);

            ApiStatics.Kis kis = new ApiStatics.Kis();
            kis.setAppKey("key");
            kis.setAppSecret("secret");
            when(apiStatics.getKis())
                    .thenReturn(kis);

            when(pm.getMode())
                    .thenReturn(TradeMode.TRAIN);

            when(webClientConnector.<Map<String, String>, String>connectKisApiBuilder())
                    .thenReturn(connectKisApiBuilder);

            when(connectKisApiBuilder.method(HttpMethod.POST)).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.path("/oauth2/tokenP")).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.requestHeaders(null)).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.requestParams(null)).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.requestBody(any(Map.class))).thenReturn(connectKisApiBuilder);
            when(connectKisApiBuilder.classType(String.class)).thenReturn(connectKisApiBuilder);

            when(tokenRepository.findAll())
                    .thenReturn(List.of(expToken));

            // when

            Field field1 = TokenService.class.getDeclaredField("token");
            field1.setAccessible(true);
            field1.set(tokenService, expToken);
            when(connectKisApiBuilder.build())
                    .thenReturn(ResponseEntity.ok(badResponse));
            // then
            assertThrows(APIException.class, () -> tokenService.checkGetToken());
        }
    }

}