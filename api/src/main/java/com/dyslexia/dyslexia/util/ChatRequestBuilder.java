package com.dyslexia.dyslexia.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRequestBuilder {

  private static final String MODEL = "model";
  private static final String ROLE = "role";
  private static final String CONTENT = "content";
  private static final String SYSTEM = "system";
  private static final String USER = "user";
  private static final String MESSAGES = "messages";
  private static final String TEMPERATURE = "temperature";
  List<Map<String, String>> messages = new ArrayList<>();
  private String model;
  private double temperature;
  private String systemPrompt;
  private String userPrompt;

  public ChatRequestBuilder model(String model) {
    this.model = model;
    return this;
  }

  public ChatRequestBuilder temperature(double temperature) {
    this.temperature = temperature;
    return this;
  }

  public ChatRequestBuilder systemMessage(String systemPrompt) {
    this.systemPrompt = systemPrompt;
    return this;
  }

  public ChatRequestBuilder userMessage(String userPrompt) {
    this.userPrompt = userPrompt;
    return this;
  }

  public Map<String, Object> build() {

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put(MODEL, model);

    Map<String, String> systemMessage = new HashMap<>();

    systemMessage.put(ROLE, SYSTEM);
    systemMessage.put(CONTENT, systemPrompt);

    messages.add(systemMessage);

    Map<String, String> userMessage = new HashMap<>();

    userMessage.put(ROLE, USER);
    userMessage.put(CONTENT, userPrompt);

    messages.add(userMessage);

    requestBody.put(MESSAGES, messages);
    requestBody.put(TEMPERATURE, temperature);

    return requestBody;
  }
}
