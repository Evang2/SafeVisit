package com.example.safevisit.api.response;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;

    public static class Main {
        public float temp;
        public int humidity;
    }

    public static class Weather {
        public String description;
        public String icon;
    }
}
