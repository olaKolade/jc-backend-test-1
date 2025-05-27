package com.virginholidays.backend.test.service;

import com.virginholidays.backend.test.repository.FlightInfoRepository;
import com.virginholidays.backend.test.repository.FlightInfoRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * The FlightInfoServiceImpl unit tests
 *
 * @author Geoff Perks
 */
@ExtendWith(MockitoExtension.class)
class FlightInfoServiceImplTest {
    @Mock
    private FlightInfoRepository repository;

    @InjectMocks
    private FlightInfoServiceImpl service;

    @Test
    void getAllFlightInfos() {
        LocalDate now = LocalDate.now();

        service.findFlightByDate(now);

        verify(repository, never()).findAll();
        verify(repository).findFlightByDate(now);
    }
}