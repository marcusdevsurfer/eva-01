package com.ai.eva_01.chat;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;

@RestController
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/code")
    public Flux<String> code(@RequestParam(value = "request", defaultValue = "Dame SaaS con Spring boot") String request) {
        return chatClient.prompt()
                .system("""
                        Todas las respuestas son en español, solo el codido es en ingles.
                        Eres un asistente de codigo, te dare un requerimiento y me ayudara a crear el codigo.
                         """)
                .user(request)
                .stream()
                .content();
    }

    @GetMapping("/chat")

    public String chat() {
        return chatClient.prompt()
                .system("""
                        All your answers should be in Spanish.
                        Text should be short and concise.
                        """)
                .user(""" 
                        Give me a joke.
                        """)
                .call()
                .content();
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzePdf(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Reading the file...");
            // Extract text from PDF
            PDDocument document = Loader.loadPDF(file.getBytes());
            String extractedText = new PDFTextStripper().getText(document);
            document.close();
            System.out.println("File already read...");
            System.out.println("Start to processing");
            String improvedText = chatClient.prompt()
                    .system("""
                            Todas las respuestas son en español
                            Eres un analizador de tareas, te dare un pdf lo analizaras y veras que puedes mejorar
                            """)
                    .user(extractedText)
                    .call()
                    .content();
            System.out.println("Processing finished");
            return ResponseEntity.ok(improvedText);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing PDF: " + e.getMessage());
        }
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
