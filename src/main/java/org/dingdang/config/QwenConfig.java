package org.dingdang.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QwenConfig {

    @Value("${llm.qwen.api-key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

}
