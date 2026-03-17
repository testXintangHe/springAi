package com.example.demoai.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class ChatController {
    @Resource
    private ChatClient chatClient;

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String question) {
        // 先将所有数据放入messages里面，包含当前用户的问题以及历史问题 (先放历史问题和历史答案，再放新的)
        // UserMessage是用户的消息
        // AssistantMessage是大模型的返回
        // 这样就可以实现上下文的功能，因为大模型是没有记忆的，得将历史数据传递过去
        // 在传递历史的时候，需要进行token计数和截断，也就是判断总的字数是否超出限制，因为大模型会有限制，一次只能处理多少字的问题 (每个大模型都不一样)
        // token不是单纯的字，对于英文，一个单词就是一个token，对于标点符号，一个标点符号就是一个token，对于中文，一个词组就是一个token(比如人工智能就是 -- 人工 + 智能 2个token)，token的计算有对应的类
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage("上一次的问题"));
        messages.add(new AssistantMessage("上一次的回答"));
        messages.add(new UserMessage(question));

        Prompt prompt = new Prompt(messages);

        // call方法会等大模型将所有结果生成在一起返回
//        String response = chatClient.prompt(prompt)
//                .call()
//                .content();

        // stream方法会实时同步大模型的结果，也就是以流的形式逐字返回
        Flux<String> response1 = chatClient.prompt(prompt)
                .stream()
                .content();
        return response1;
    }
}
