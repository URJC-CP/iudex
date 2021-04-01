package com.example.aplicacion.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONConverter {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String convertObjectToJSON(Object obj) throws JsonProcessingException {
		if (obj == null) {
			throw new RuntimeException("Invalid object!");
		}
		return objectMapper.writeValueAsString(obj);
	}

	public Object convertJSONToObject(String json, Class cls) throws IOException {
		return objectMapper.readValue(json, cls);
	}
}
