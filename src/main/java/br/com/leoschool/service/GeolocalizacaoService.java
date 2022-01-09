package br.com.leoschool.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;

import br.com.leoschool.model.Contato;

@Service
public class GeolocalizacaoService {
	
	public List<Double> obterLatitudeLongitude(Contato contato) throws ApiException, InterruptedException, IOException {
		GeoApiContext geoApiContext = new GeoApiContext().setApiKey("AIzaSyD7MMqoSM1qzPQbb-NJXU56WTzMGfJ8RWA");
		
		GeocodingApiRequest geocodingApiRequest = GeocodingApi.newRequest(geoApiContext).address(contato.getEndereco());
		
		GeocodingResult[] response = geocodingApiRequest.await();
		
		GeocodingResult geocodingResult = response[0];
		
		Geometry geometry = geocodingResult.geometry;
		
		LatLng location = geometry.location;
		
		return Arrays.asList(location.lat, location.lng);
	}
	
}
