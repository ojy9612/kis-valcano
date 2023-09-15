package com.zeki.kisvolcano.domain.data_go.holiday.service;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain.data_go.holiday.entity.Holiday;
import com.zeki.kisvolcano.domain.data_go.holiday.repository.HolidayRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class HolidayServiceTest {
    @Autowired
    HolidayService holidayService;

    @Autowired
    WebClientConnector webClientConnector;

    @Autowired
    HolidayRepository holidayRepository;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {
        @Test
        @DisplayName("공휴일 아닌 날짜 Get")
        @Transactional
        void getAvailableDate() {
            // case
            LocalDate now;
            if (LocalTime.now().isBefore(LocalTime.of(16, 0))) {
                now = LocalDate.now().minusDays(1);
            } else {
                now = LocalDate.now();
            }
            Holiday testHoliday = Holiday.builder()
                    .name("now")
                    .date(now)
                    .build();

            Holiday testHoliday2 = Holiday.builder()
                    .name("m 2")
                    .date(now.minusDays(2))
                    .build();

            Holiday testHoliday4 = Holiday.builder()
                    .name("m 4")
                    .date(now.minusDays(4))
                    .build();

            Holiday testHoliday5 = Holiday.builder()
                    .name("m 5")
                    .date(now.minusDays(5))
                    .build();

            holidayRepository.save(testHoliday);
            holidayRepository.save(testHoliday2);
            holidayRepository.save(testHoliday4);
            holidayRepository.save(testHoliday5);

            // when
            LocalDate availableDate = holidayService.getAvailableDate();
            LocalDate deltaOneAvailableDate = holidayService.deltaOneAvailableDate();
            LocalDate deltaTwoAvailableDate = holidayService.deltaTwoAvailableDate();

            // then
            assertEquals(now.minusDays(1), availableDate);
            assertEquals(now.minusDays(3), deltaOneAvailableDate);
            assertEquals(now.minusDays(6), deltaTwoAvailableDate);
        }

        @Test
        @DisplayName("공휴일 생성")
        @Transactional
        void createHolidaysByYear() {
            // case
            int year = LocalDate.now().getYear();
            holidayService.createHolidaysByYear(year);

            // when
            Optional<Holiday> one1 = holidayRepository.findByDate(LocalDate.of(year, 1, 1));
            Optional<Holiday> five5 = holidayRepository.findByDate(LocalDate.of(year, 5, 5));
            Optional<Holiday> twelve25 = holidayRepository.findByDate(LocalDate.of(year, 12, 25));


            // then
            assertTrue(one1.isPresent());
            assertTrue(five5.isPresent());
            assertTrue(twelve25.isPresent());
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

    }
}