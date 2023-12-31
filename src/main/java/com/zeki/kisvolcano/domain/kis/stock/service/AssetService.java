package com.zeki.kisvolcano.domain.kis.stock.service;

import com.zeki.kisvolcano.config.PropertiesMapping;
import com.zeki.kisvolcano.domain._common.em.TradeMode;
import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain._common.web_client.statics.ApiStatics;
import com.zeki.kisvolcano.domain.kis.stock.dto.AccountDataResDto;
import com.zeki.kisvolcano.domain.kis.token.service.TokenService;
import com.zeki.kisvolcano.exception.APIException;
import com.zeki.kisvolcano.exception.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final WebClientConnector webClientConnector;
    private final TokenService tokenService;

    private final PropertiesMapping pm;
    private final ApiStatics apiStatics;

    /**
     * 현재 매수 내역, 계좌 현황 정보를 가져온다.
     *
     * @return List<AccountDataResDto>
     */
    public List<AccountDataResDto> getAccountData() {

        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
        Map<String, String> reqHeaders = new HashMap<>();

        reqHeaders.put("authorization", tokenService.checkGetToken());
        reqHeaders.put("appkey", apiStatics.getKis().getAppKey());
        reqHeaders.put("appsecret", apiStatics.getKis().getAppSecret());
        reqHeaders.put("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC8434R" : "VTTC8434R");

        reqParams.add("CANO", apiStatics.getKis().getAccountNumber());
        reqParams.add("ACNT_PRDT_CD", "01");
        reqParams.add("AFHR_FLPR_YN", "N");
        reqParams.add("OFL_YN", "");
        reqParams.add("INQR_DVSN", "01");
        reqParams.add("UNPR_DVSN", "01");
        reqParams.add("FUND_STTL_ICLD_YN", "N");
        reqParams.add("FNCG_AMT_AUTO_RDPT_YN", "N");
        reqParams.add("PRCS_DVSN", "01");
        reqParams.add("CTX_AREA_FK100", "");
        reqParams.add("CTX_AREA_NK100", "");


        List<AccountDataResDto> responseList = new ArrayList<>();

        String trCont = "F";

        while (trCont.equals("F") || trCont.equals("M")) {
            ResponseEntity<AccountDataResDto> response = webClientConnector.
                    <Map<String, String>, AccountDataResDto>connectKisApiBuilder()
                    .method(HttpMethod.GET)
                    .path("/uapi/domestic-stock/v1/trading/inquire-balance")
                    .requestHeaders(reqHeaders)
                    .requestParams(reqParams)
                    .requestBody(reqBody)
                    .classType(AccountDataResDto.class)
                    .build();

            List<String> tempTrCont = response.getHeaders().getOrDefault("tr_cont", Collections.singletonList(""));
            trCont = tempTrCont.get(0);

            AccountDataResDto body = response.getBody();
            responseList.add(body);

            try {
                reqParams.set("CTX_AREA_FK100", trCont.equals("F") || trCont.equals("M") ? body.getCtxAreaFk100() : "");
                reqParams.set("CTX_AREA_NK100", trCont.equals("F") || trCont.equals("M") ? body.getCtxAreaNk100() : "");
            } catch (NullPointerException e) {
                throw new APIException(ResponseCode.RESOURCE_NOT_FOUND, "getAccountData의 다음 데이터 존재하지 않습니다.");
            }

        }

        return responseList;
    }
}
