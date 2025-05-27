package com.virginholidays.backend.test.resource;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.service.FlightInfoService;
import com.virginholidays.backend.test.service.FlightInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * The FlightInfoResource unit tests
 *
 * @author Geoff Perks
 */
@SpringBootTest
@AutoConfigureMockMvc
class FlightInfoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightInfoService flightInfoService;

    @Test
    void getFlightInfo() throws Exception {
        mockMvc.perform(get("/389292/results"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid date format. Allowed formats are: yyyy-MM-dd"));
    }

    @Test
    void getFlightInfo2() throws Exception {
        String expectedString = Files.readString(new ClassPathResource("/ok_sunday_response.json").getFile().toPath(), StandardCharsets.UTF_8);
        String localDateString = "2025-05-27";
        LocalDate now = LocalDate.parse(localDateString);

        Flight flight = new Flight(LocalTime.parse("09:00"), "St Lucia", "UVF", "VS097", List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        Flight flight2 = new Flight(LocalTime.parse("09:00"), "Tobago", "TAB", "VS097", List.of(DayOfWeek.SUNDAY));

        when(flightInfoService.findFlightByDate(now)).thenReturn(completedFuture(Optional.of(List.of(flight, flight2))));

        mockMvc.perform(get(String.format("/%s/results", localDateString)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedString));
    }

    @Test
    void getFlightInf() throws Exception {
        String localDateString = "2025-05-27";
        LocalDate now = LocalDate.parse(localDateString);
        when(flightInfoService.findFlightByDate(now)).thenThrow(new UncheckedIOException(new IOException()));

        mockMvc.perform(get(String.format("/%s/results", localDateString)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unable to load flight data. Please try again later."));
    }
}

