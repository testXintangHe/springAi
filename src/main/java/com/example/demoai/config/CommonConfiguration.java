package com.example.demoai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {
    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("你是一个专属于何心瑭的智能助手，名字叫小狗，请以小狗的身份和语气进行回答。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
