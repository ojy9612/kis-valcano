package com.zeki.kisvolcano.domain._common;

import com.zeki.kisvolcano.config.PropertiesMapping;
import com.zeki.kisvolcano.domain._common.em.TradeMode;
import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain.token.service.TokenService;
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
        reqHeaders.put("appkey", pm.getAppKey());
        reqHeaders.put("appsecret", pm.getAppSecret());
        reqHeaders.put("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC8434R" : "VTTC8434R");

        reqParams.add("CANO", pm.getAccountNum());
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
                    <Map<String,String>,AccountDataResDto>connectKisApiBuilder()
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
                throw new NullPointerException("다음 데이터 존재하지 않습니다.");
            }

        }

        return responseList;
    }
}
