package com.zeki.kisvolcano.exception;

import com.zeki.kisvolcano.domain._common.dto.CommonResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("rawtypes")
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice extends Exception {

    private void loggingTextWarn(Exception e, String message) {
        /* 내 프로젝트에서 난 첫번째 에러만 표시하는 로직 */
        String projectPrefix = "com.zeki";
        StackTraceElement projectStackTraceElement = null;

        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().startsWith(projectPrefix)) {
                projectStackTraceElement = element;
                break;
            }
        }

        if (projectStackTraceElement != null) {
            log.warn(message + " | EndPoint : " + projectStackTraceElement);
        } else {
            log.warn(message + " | EndPoint not found in project");
        }
        /* ----- */
    }

    private void loggingTextWarn(Exception e) {
        /* 내 프로젝트에서 난 첫번째 에러만 표시하는 로직 */
        String projectPrefix = "com.zeki";
        StackTraceElement projectStackTraceElement = null;

        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().startsWith(projectPrefix)) {
                projectStackTraceElement = element;
                break;
            }
        }

        if (projectStackTraceElement != null) {
            log.warn(e.getMessage() + " | EndPoint : " + projectStackTraceElement);
        } else {
            log.warn(e.getMessage() + " | EndPoint not found in project : " + e.getStackTrace()[0]);
        }
        /* ----- */
    }

    private void loggingTextError(Exception e) {
        /* 내 프로젝트에서 난 첫번째 에러만 표시하는 로직 */
        String projectPrefix = "com.zeki";
        StackTraceElement projectStackTraceElement = null;

        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().startsWith(projectPrefix)) {
                projectStackTraceElement = element;
                break;
            }
        }

        if (projectStackTraceElement != null) {
            log.error(e.getMessage() + " | EndPoint : " + projectStackTraceElement);
        } else {
            log.error(e.getMessage() + " | EndPoint not found in project : " + e.getStackTrace()[0]);
        }
        /* ----- */
    }

    /**
     * 커스텀 예외처리 함수
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<CommonResDto> handleAPIException(APIException e) {


        loggingTextWarn(e);

        return ResponseEntity.status(e.getResponseCode().getStatus())
                .body(CommonResDto.error(e.getResponseCode().getCode(), e.getMessage()));
    }

    /**
     * 유효성검사 실패
     * 어느 변수에 무엇이 잘못되었는지 message에 작성한대로 보여준다.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<CommonResDto<Map<String, String>>> handleBindException(BindException e) {

        Map<String, String> data = new HashMap<>();

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            data.put(((DefaultMessageSourceResolvable) Objects.requireNonNull(error.getArguments())[0]).getDefaultMessage()
                    , error.getDefaultMessage());
        }

        log.warn(data + " | EndPoint : " + e.getStackTrace()[0].toString());
        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.BINDING_FAILED.getStatus())
                .body(CommonResDto.error(ResponseCode.BINDING_FAILED.getCode(), ResponseCode.BINDING_FAILED.getDefaultMessage()));
    }

    /**
     * requestBody가 비어있을 시 발생하는 예외
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.REQUIRED_BODY.getStatus())
                .body(CommonResDto.error(ResponseCode.REQUIRED_BODY.getCode(), ResponseCode.REQUIRED_BODY.getDefaultMessage()));
    }


    /**
     * new Entity(id) 사용시 id에 해당하는 데이터가 없을 시 발생하는 예외
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<CommonResDto> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {

        this.loggingTextWarn(e, "외래키의 ID가 존재하지 않습니다.");
        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.RESOURCE_NOT_FOUND.getStatus()).body(CommonResDto.error(ResponseCode.RESOURCE_NOT_FOUND.getCode(), ResponseCode.RESOURCE_NOT_FOUND.getDefaultMessage()));
    }


    /**
     * SQLException 예외처리
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<CommonResDto> handleSQLException(SQLException e) {

        this.loggingTextError(e);
        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus()).body(CommonResDto.error(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ResponseCode.INTERNAL_SERVER_ERROR.getDefaultMessage()));
    }


    /**
     * SQL connection 예외처리
     */
    @ExceptionHandler(SQLNonTransientConnectionException.class)
    public ResponseEntity<CommonResDto> handleSQLNonTransientConnectionException(SQLNonTransientConnectionException e) {

        this.loggingTextError(e);
        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus()).body(CommonResDto.error(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ResponseCode.INTERNAL_SERVER_ERROR.getDefaultMessage()));
    }


    /**
     * 낙관적 락 예외처리
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<CommonResDto> handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {

        this.loggingTextError(e);
        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.OPTIMISTIC_LOCKING_FAILURE.getStatus()).body(CommonResDto.error(ResponseCode.OPTIMISTIC_LOCKING_FAILURE.getCode(), ResponseCode.OPTIMISTIC_LOCKING_FAILURE.getDefaultMessage()));
    }

    /**
     * 모든 예외를 처리하는 함수, 간단한 에러 표시를 통해 LogBack으로 확인하기 편하게 한다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResDto> handleException(Exception e) {

        this.loggingTextError(e);
        e.printStackTrace();

        return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus()).body(CommonResDto.error(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ResponseCode.INTERNAL_SERVER_ERROR.getDefaultMessage()));
    }


}
