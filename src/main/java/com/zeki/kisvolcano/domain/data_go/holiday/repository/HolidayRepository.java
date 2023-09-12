package com.zeki.kisvolcano.domain.data_go.holiday.repository;

import com.zeki.kisvolcano.domain.data_go.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    Optional<Holiday> findByDate(LocalDate date);
}
