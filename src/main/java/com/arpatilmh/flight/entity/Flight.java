package com.arpatilmh.flight.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Flight {
    private String flightNumber;
    private String from;
    private String to;
    private int start;
    private int duration;
}