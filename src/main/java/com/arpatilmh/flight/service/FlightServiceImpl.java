package com.arpatilmh.flight.service;

import com.arpatilmh.flight.entity.Flight;
import com.arpatilmh.flight.repository.IFlightRepository;
import com.arpatilmh.flight.repository.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlightServiceImpl implements IFlightService {
    @Autowired
    private IFlightRepository flightRepository;

    @Override
    public List<Map<String, Map<String, Integer>>> getFastestRoutes(String from, String to, int noOfFlights) {
        PriorityQueue<Route> possibleRoutes = new PriorityQueue<>();
        // Calculate all direct flights
        List<Flight> directFlights = flightRepository.getFlights(from, to);
        for(Flight flight : directFlights) {
            possibleRoutes.offer(toRoute(flight));
        }

        // Calculate all indirect flights with 1 stop
        List<Route> oneStopFlights = flightRepository.getFlightsOneStop(from, to);
        possibleRoutes.addAll(oneStopFlights);

        Map<String, Map<String, Integer>> directRoutes = new HashMap<>();
        Map<String, Map<String, Integer>> oneStopRoutes = new HashMap<>();
        for(int i=0; i<noOfFlights; ) {
            if (possibleRoutes.isEmpty()) {
                break;
            }

            Route route = possibleRoutes.poll();
            if (route.getCities().chars().filter(ch -> ch == '_').count() == 1 && !directRoutes.containsKey(route.getCities())) {
                directRoutes.put(route.getCities(), Map.of(route.getCodes(), route.getDuration()));
                i++;
            }

            if (route.getCities().chars().filter(ch -> ch == '_').count() == 2 && !oneStopRoutes.containsKey(route.getCities())) {
                oneStopRoutes.put(route.getCities(), Map.of(route.getCodes(), route.getDuration()));
                i++;
            }
        }

        List<Map<String, Map<String, Integer>>> result = new ArrayList<>();
        if (!directRoutes.isEmpty()) {
            result.add(directRoutes);
        }

        if (!oneStopRoutes.isEmpty()) {
            result.add(oneStopRoutes);
        }
        return result;
    }

    private Route toRoute(Flight flight) {
        return new Route(flight.getFrom() + "_" + flight.getTo(), flight.getFlightNumber(), flight.getDuration());
    }
}
