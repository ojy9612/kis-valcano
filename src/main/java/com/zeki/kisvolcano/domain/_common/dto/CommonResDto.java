package com.zeki.kisvolcano.domain._common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zeki.kisvolcano.exception.ResponseCode;
import lombok.Getter;

import java.io.Serializable;


@Getter
public class CommonResDto<T> implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL) // 필드가 null 일 때 직렬화 제외
    private T data;
    private final String code;
    private final String message;

    public CommonResDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // 리턴할 데이터가 있을 때 성공 응답
    public static <T> CommonResDto<T> success(T data) {
        ResponseCode ok = ResponseCode.OK;
        return new CommonResDto<>(ok.getCode(), ok.getDefaultMessage(), data);
    }

    // 리턴할 데이터가 없을 때 성공 응답
    public static <T> CommonResDto<T> success() {
        ResponseCode ok = ResponseCode.OK;
        return new CommonResDto<>(ok.getCode(), ok.getDefaultMessage());
    }

    // 리턴할 데이터가 있을 때 에러 응답
    public static <T> CommonResDto<T> error(String code, String message, T data) {
        return new CommonResDto<>(code, message, data);
    }

    // 리턴할 데이터가 없을 때 에러 응답
    public static <T> CommonResDto<T> error(String code, String message) {
        return new CommonResDto<>(code, message);
    }
}