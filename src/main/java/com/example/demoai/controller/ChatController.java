package com.example.demoai.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Media;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.*;

import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.FILTER_EXPRESSION;

@RestController
@RequestMapping("/ai")
public class ChatController {
    @Resource(name = "chatClient")
    private ChatClient chatClient;

    @Resource(name = "pdfChatClient")
    private ChatClient pdfChatClient;

    @Resource(name = "mediaChatClient")
    private ChatClient mediaChatClient;

    @Autowired
    private VectorStore vectorStore;

    private Map<String, String> history = new LinkedHashMap<>();

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

    @RequestMapping(value = "/service", produces = "text/html;charset=utf-8")
    public Flux<String> service(String prompt) {
        long curTime = System.currentTimeMillis();
        history.put("user" + curTime, prompt);

        List<Message> messages = new ArrayList<>();
        for (Map.Entry<String, String> entry : history.entrySet()) {
            if (entry.getKey().startsWith("user")) {
                messages.add(new UserMessage(entry.getValue()));
            } else {
                messages.add(new AssistantMessage(entry.getValue()));
            }
        }
        Prompt realPrompt = new Prompt(messages);

        Flux<String> response = chatClient.prompt(realPrompt)
                .stream()
                .content();

        // 获取流式响应，且在流结束的时候将结果拼接，然后存入history，这样既可以存储结果，也不阻塞响应
        StringBuilder fullAnswer = new StringBuilder();
        return response.doOnNext(chunk -> fullAnswer.append(chunk))
                .doOnComplete(() -> {
                    history.put("assistant" + curTime, fullAnswer.toString());
                });
    }

    @RequestMapping(value = "/pdfChat", produces = "text/html;charset=utf-8")
    public Flux<String> pdfChat(String prompt) {
        // 写入向量库
        org.springframework.core.io.Resource resource = new ClassPathResource("templates/Spring Ai.pdf");
        this.writeToVectorStore(resource);

        // 获取结果
        return pdfChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(FILTER_EXPRESSION, "file_name == '" + "Spring Ai.pdf" + "'")) // 可能会有很多文档，然后这个只对 springAi.txt 进行向量检索
                .stream()
                .content();
    }

    private void writeToVectorStore(org.springframework.core.io.Resource resource) {
        // 1.创建读取器
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                resource, // 文件源
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1) // 每1页PDF作为一个Document
                        .build()
        );
        // 2.读取文档，拆分为Document
        List<Document> documents = reader.read();
        // 3.写入向量库
        vectorStore.delete(List.of("*")); // 这是防止重复写入，因为我为了省事，直接给接口里面加了写入代码，所以每次调用都写入
        vectorStore.add(documents);
    }

    @RequestMapping(value = "/media", produces = "text/html;charset=utf-8")
    public Flux<String> media(String prompt) {
        org.springframework.core.io.Resource resource = new ClassPathResource("templates/test.mp3");
        Media media = new Media(MimeType.valueOf("audio/mp3"), resource);

        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(prompt, media));

        Prompt realPrompt = new Prompt(messages);

        Flux<String> response = mediaChatClient.prompt(realPrompt)
                .stream()
                .content();

       return response;
    }
}
