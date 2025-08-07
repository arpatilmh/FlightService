package com.arpatilmh.flight.repository;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Route implements Comparable<Route> {
    private String cities;
    private String codes;
    private int duration;


    @Override
    public int compareTo(Route o) {
        return Integer.compare(this.duration, o.duration);
    }
}
