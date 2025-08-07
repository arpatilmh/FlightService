package com.arpatilmh.flight.repository;

import com.arpatilmh.flight.entity.Flight;

import java.util.List;

public interface IFlightRepository {
    List<Flight> getFlights(String from, String to);
    List<Route> getFlightsOneStop(String from, String to);
}
