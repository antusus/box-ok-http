package pl.kamil.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class MyJsonMapper {
  private static final MyJsonMapper INSTANCE = new MyJsonMapper();
  private final ObjectMapper objectMapper;

  private MyJsonMapper() {
    this.objectMapper =
        new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES).disable(WRITE_DATES_AS_TIMESTAMPS);
  }

  public static MyJsonMapper getInstance() {
    return INSTANCE;
  }

  public JsonNode parse(String jsonString) {
    try {
      return objectMapper.readTree(jsonString);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Couldn't parse JSON", e);
    }
  }

  public String toJsonString(Object value) {
    try {
      return this.objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Couldn't serialize object", e);
    }
  }

  public byte[] serailize(Object obj) {
    try {
      return objectMapper.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Couldn't serialize object", e);
    }
  }

  public <T> T deserialize(String jsonString, Class<T> destinationClass) {
    try {
      return objectMapper.readValue(jsonString, destinationClass);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Cannot deserialize JSON", e);
    }
  }

  public String toJsonString(InputStream byteStream) {
    try {
      return objectMapper.readTree(byteStream).toString();
    } catch (IOException e) {
      throw new RuntimeException("Cannot read response", e);
    }
  }
}
