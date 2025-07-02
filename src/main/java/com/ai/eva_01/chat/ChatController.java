package com.ai.eva_01.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    private final ChatClient chatClient;
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .system("""
                        All your answers should be in Spanish.
                        Text should be short and concise.
                        """)
                .user(""" 
                        Tell me a easy way to earn money with Java and Spring.
                        """)
                .call()
                .content();
    }

    @GetMapping("/stream")
    public Flux<String> stream() {
        return chatClient.prompt()
                .user(""" 
                        Tell me a easy way to earn money with Java and Spring.
                        """)
                .stream()
                .content();
    }

}
