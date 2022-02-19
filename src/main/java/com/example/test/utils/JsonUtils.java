package com.example.test.utils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JsonUtils 
{

	ObjectMapper objectMapper= getMapper();
	
	ObjectNode parentNode = objectMapper.createObjectNode();
	
	@Bean
	public ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		 mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		 mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// mapper.registerModule(new JavaTimeModule());
	        SimpleModule module = new SimpleModule();
	        module.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
	            @Override
	            public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
	                String value = jsonParser.getText();
	                return parseDateTimeString(value);
	            }
	        });
	        mapper.registerModule(module);
		return  mapper;
	}

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ISO_DATE_TIME)
            .appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .appendOptional(DateTimeFormatter.ISO_INSTANT)
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SX"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .toFormatter()
            .withZone(ZoneOffset.UTC);
    
	public static OffsetDateTime parseDateTimeString(String str) {
        return ZonedDateTime.from(DATE_TIME_FORMATTER.parse(str)).toOffsetDateTime();
    }
	
	public  JsonNode setData(String value)
	{
		
		 parentNode.put("data", value);
		 return parentNode;
	}
	public  JsonNode setError(String value)
	{
		parentNode.put("error", true);
		 parentNode.put("message", value);
		 return parentNode;
	}
	
	public String print(Object value) 
	{		
		try
		{
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		}
		catch(JsonProcessingException e)
		{
			log.error(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	
}
