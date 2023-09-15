package com.zeki.kisvolcano.config;

import com.zeki.kisvolcano.domain._common.em.TradeMode;
import com.zeki.kisvolcano.exception.APIException;
import com.zeki.kisvolcano.exception.ResponseCode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PropertiesMapping {

    @Value("${spring.config.activate.on-profile}")
    private String profile;

    @Getter
    private TradeMode mode;

    @PostConstruct
    public void init() {
        mode = TradeMode.getEnum(profile);

        if (mode.equals(TradeMode.TRAIN)) {
            log.info("모의투자 계좌로 세팅 되었습니다. mode : " + mode.getDescription());
        } else if (mode.equals(TradeMode.REAL)) {
            log.info("실 계좌로 세팅 되었습니다. mode : " + mode.getDescription());
        } else if (mode.equals(TradeMode.TEST)) {
            log.info("테스트(모의투자)로 세팅 되었습니다. mode : " + mode.getDescription());
        } else {
            throw new APIException(ResponseCode.INVALID_PROFILE, "허용되지 않은 profile 입니다. - " + profile);
        }

    }

}
