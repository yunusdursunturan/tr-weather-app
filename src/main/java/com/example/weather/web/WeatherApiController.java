package com.example.weather.web;

import com.example.weather.entity.WeatherEntity;
import com.example.weather.integration.ows.Weather;
import com.example.weather.integration.ows.WeatherEntry;
import com.example.weather.integration.ows.WeatherForecast;
import com.example.weather.integration.ows.WeatherService;

import com.example.weather.repository.WeatherRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherApiController {

	private final WeatherService weatherService;
	private final WeatherRepository weatherRepository;

	public WeatherApiController(WeatherService weatherService, WeatherRepository weatherRepository) {
		this.weatherService = weatherService;
		this.weatherRepository = weatherRepository;
	}

	@RequestMapping("/now/{country}/{city}")
	public Weather getWeather(@PathVariable String country,
			@PathVariable String city) {
		return this.weatherService.getWeather(country, city);
	}

	@RequestMapping("/weekly/{country}/{city}")
	public WeatherForecast getWeatherForecast(@PathVariable String country,
			@PathVariable String city) {
		return this.weatherService.getWeatherForecast(country, city);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<WeatherEntity> getAll() {
		return weatherRepository.findAll();
	}
}
