package com.arpatilmh.flight.repository;

import com.arpatilmh.flight.entity.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


@Service
public class InMemoryFlightRepository implements IFlightRepository {

    @Autowired
    private ResourceLoader resourceLoader;

    private final Map<String, Map<String, List<Flight>>> flightData;
    private final int MINUTES_IN_DAY = 24 * 60;

    @PostConstruct
    public void init() throws IOException, IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("ivtest-sched.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String flightNo = parts[0];
                    String from = parts[1];
                    String to = parts[2];
                    int startTime = getTimeStamp(parts[3]);
                    int endTime = getTimeStamp(parts[4]);

                    Flight flight = new Flight(flightNo, from, to, startTime, (endTime - startTime + MINUTES_IN_DAY) % MINUTES_IN_DAY);
                    flightData.computeIfAbsent(from, k -> new HashMap<>())
                            .computeIfAbsent(to, k -> new ArrayList<>())
                            .add(flight);
                }
            }
        }
    }

    private int getTimeStamp(String time) {
        String modifiedTime = String.format("%04d", Integer.parseInt(time));
        int hour = Integer.parseInt(modifiedTime.substring(0, 2));
        int minute = Integer.parseInt(modifiedTime.substring(2, 4));
        return hour * 60 + minute;
    }

    public InMemoryFlightRepository() {
        this.flightData = new HashMap<>();
    }

    @Override
    public List<Flight> getFlights(String from, String to) {
        return flightData.getOrDefault(from, Collections.emptyMap())
                         .getOrDefault(to, Collections.emptyList());
    }

    @Override
    public List<Route> getFlightsOneStop(String from, String to) {
        // All possible intermediate stops
        List<String> intermediateStops = new ArrayList<>(flightData.getOrDefault(from, Collections.emptyMap()).keySet());

        // Filter intermediate stops that have flights to the destination
        List<String> intermediateStopsFiltered = new ArrayList<>();
        for(String stop: intermediateStops) {
            if (flightData.getOrDefault(stop, Collections.emptyMap()).containsKey(to)) {
                intermediateStopsFiltered.add(stop);
            }
        }

        List<Route> possibleRoutes = new ArrayList<>();
        for(String stop : intermediateStopsFiltered) {
            List<Flight> flightsFromFromToStop = flightData.getOrDefault(from, Collections.emptyMap()).getOrDefault(stop, Collections.emptyList());
            List<Flight> flightsFromStopToTo = flightData.getOrDefault(stop, Collections.emptyMap()).getOrDefault(to, Collections.emptyList());

            for (Flight flight1 : flightsFromFromToStop) {
                for (Flight flight2 : flightsFromStopToTo) {
                    String cities = from + "_" + stop + "_" + to;
                    String codes = flight1.getFlightNumber() + "_" + flight2.getFlightNumber();
                    if (flight1.getStart() + flight1.getDuration() + 120 >= MINUTES_IN_DAY) {
                        // flight landed next day
                        int flight1EndTimeNextDay = (flight1.getStart() + flight1.getDuration() + 120) % MINUTES_IN_DAY; // Adding 2 hours buffer
                        if (flight1EndTimeNextDay <= flight2.getStart()) {
                            // If flight1 ends before or at the start of flight2 on the next day
                            int journeyDuration = MINUTES_IN_DAY - flight1.getStart() + flight2.getStart() + flight2.getDuration();
                            possibleRoutes.add(new Route(cities, codes, journeyDuration));
                        } else {
                            // If flight1 ends after the start of flight2 on the next day
                            int journeyDuration = MINUTES_IN_DAY - flight1.getStart() + flight2.getStart() + flight2.getDuration() + MINUTES_IN_DAY;
                            possibleRoutes.add(new Route(cities, codes, journeyDuration));
                        }
                    } else {
                        // flight landed on the same day
                        int flight1EndTime = flight1.getStart() + flight1.getDuration() + 120;
                        if (flight1EndTime <= flight2.getStart()) {
                            int journeyDuration = flight2.getStart() + flight2.getDuration() - flight1.getStart();
                            possibleRoutes.add(new Route(cities, codes, journeyDuration));
                        } else {
                            // If flight1 ends after the start of flight2 on the same day
                            int journeyDuration = MINUTES_IN_DAY - flight1.getStart() + flight2.getStart() + flight2.getDuration();
                            possibleRoutes.add(new Route(cities, codes, journeyDuration));
                        }
                    }
                }
            }
        }

        return possibleRoutes;
    }
}
