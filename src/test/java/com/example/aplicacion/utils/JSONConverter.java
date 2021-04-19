package com.example.aplicacion.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONConverter {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String convertObjectToJSON(Object obj) {
		if (obj == null) {
			throw new RuntimeException("Invalid object!");
		}
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Object convertJSONToObject(String json, Class cls) {
		try {
			return objectMapper.readValue(json, cls);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
