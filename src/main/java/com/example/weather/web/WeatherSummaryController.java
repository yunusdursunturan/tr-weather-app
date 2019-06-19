package com.example.weather.web;

import java.util.*;

import com.example.weather.WeatherAppProperties;
import com.example.weather.entity.WeatherEntity;
import com.example.weather.integration.ows.Weather;
import com.example.weather.integration.ows.WeatherService;

import com.example.weather.repository.WeatherRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class WeatherSummaryController {

    private final WeatherService weatherService;

    private final WeatherAppProperties properties;

    private final WeatherRepository weatherRepository;

    public WeatherSummaryController(WeatherService weatherService, WeatherAppProperties properties, WeatherRepository weatherRepository) {
        this.weatherService = weatherService;
        this.properties = properties;
        this.weatherRepository = weatherRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView conferenceWeather() {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("summary", getSummary());
        return new ModelAndView("summary", model);
    }

    private Object getSummary() {
        List<WeatherSummary> summary = new ArrayList<>();
        for (String location : this.properties.getLocations()) {
            String country = location.split("/")[0];
            String city = location.split("/")[1];
            Weather weather = null;
            try {
                weather = this.weatherService.getWeather(country, city);
            } catch (HttpClientErrorException e) {
                System.out.println(e.getMessage());
            }

            if (verifyCity(city)) {
                if (weather != null) {
                    WeatherEntity weatherEntity = convertWeatherEntity(weather);
                    weatherRepository.save(weatherEntity);
                }
            }
            if (weather != null) {
                summary.add(createWeatherSummary(country, city, weather));
            }
        }
        return summary;
    }

    private boolean verifyCity(String city) {

        return !weatherRepository.findByName(city).isPresent();
    }


    private WeatherSummary createWeatherSummary(String country, String city,
                                                Weather weather) {
        if ("Las Vegas".equals(city)) {
            weather.setWeatherId(666);
        }
        return new WeatherSummary(country, city, weather);
    }

    private WeatherEntity convertWeatherEntity(Weather weather) {

        WeatherEntity weatherEntity = new WeatherEntity();

        weatherEntity.setName(weather.getName());
        weatherEntity.setTemperature(weather.getTemperature());
        weatherEntity.setTimestamp(weather.getTimestamp());
        weatherEntity.setWeatherIcon(weather.getWeatherIcon());
        weatherEntity.setWeatherId(weather.getWeatherId());

        return weatherEntity;
    }

}
