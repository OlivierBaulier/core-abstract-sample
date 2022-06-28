package com.example.demo.dto.out;

import java.util.List;

public class Stock {

    public Stock(State state, List<AvailableShoe> shoes) {
        this.state = state;
        this.shoes = shoes;
    }

    public enum State {
        EMPTY,
        FULL,
        SOME
    }

    public State     state;
    public List<AvailableShoe> shoes;
}
