package org.dingdang.common.utils;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.app.*;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import jakarta.annotation.PostConstruct;
import org.dingdang.config.QwenConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


@Component
public class QwenUtil {

    private static String apiKey;
    private static String appId;
    private static String readingSystemPrompt;

    @Autowired
    private QwenConfig qwenConfig;

    @PostConstruct
    public void init() throws IOException {
        QwenUtil.apiKey = qwenConfig.getApiKey();
        Resource resource = new ClassPathResource("templates/SystemReadingPrompt");
        Path path = Paths.get(resource.getURI());
        readingSystemPrompt = Files.readString(path);
    }

//    public static String generateReadingText(String message)
//            throws ApiException, NoApiKeyException, InputRequiredException {
//
//        ApplicationParam param = ApplicationParam.builder()
//                .apiKey(apiKey)
//                .appId("627ece74bc2347a799a866ab582403b2")
//                .prompt(message)
//                .build();
//
//        Application application = new Application();
//        ApplicationResult result = application.call(param);
//        return result.getOutput().getText();
//    }

    public static String generateReadingText(String message) throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();

        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(readingSystemPrompt)
                .build();

        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(message)
                .build();

        GenerationParam param = GenerationParam.builder()
                .model("qwen-long")
                .apiKey(apiKey)
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .build();
        return gen.call(param).getOutput().getChoices().getFirst().getMessage().getContent();
    }
}
