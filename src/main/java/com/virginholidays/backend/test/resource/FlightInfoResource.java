package com.virginholidays.backend.test.resource;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.service.FlightInfoService;

import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.CacheControl.noCache;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.status;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Geoff Perks
 *
 * The FlightInfoResource
 */
@RestController
public class FlightInfoResource {

    private final FlightInfoService flightInfoService;

    /**
     * The constructor
     *
     * @param flightInfoService the flightInfoService
     */
    public FlightInfoResource(FlightInfoService flightInfoService) {
        this.flightInfoService = flightInfoService;
    }

    /**
     * Resource method for returning flight results
     *
     * @param date the chosen date
     * @return flights for the day of the chosen date
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{date}/results")
    public ResponseEntity<?> getResults(@PathVariable("date") @NotEmpty String date) {
        LocalDate inputDate;

        try {
            inputDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return status(HttpStatus.BAD_REQUEST)
                    .cacheControl(noCache())
                    .body("Invalid date format. Allowed formats are: yyyy-MM-dd");
        }

        try {
            return flightInfoService.findFlightByDate(inputDate)
                    .handle((maybeResults, error) -> {
                        if (error != null) {
                            return status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(noCache()).body("Something went wrong. Please try again later.");
                        }

                        // no results, no content
                        if (maybeResults.isEmpty()) {
                            return status(HttpStatus.NO_CONTENT).cacheControl(noCache()).build();
                        }

                        List<Flight> results = maybeResults.get();

                        return status(HttpStatus.OK).cacheControl(noCache()).body(results);
                    })
                    .toCompletableFuture().get();
        } catch (UncheckedIOException e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(noCache()).body("Unable to load flight data. Please try again later.");
        } catch (ExecutionException | InterruptedException e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(noCache()).body("Something went wrong. Please try again later.");
        }
    }
}
