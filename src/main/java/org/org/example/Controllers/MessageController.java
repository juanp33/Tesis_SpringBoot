package org.example.Controllers;

import org.example.Models.Message;
import org.example.Services.ChatService;
import org.example.Services.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatSessionService chatSessionService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Message>> saveMessage(
            @RequestPart("chatId") String chatId,
            @RequestPart("userMessage") String userMessage,
            @RequestPart("assistantResponse") String assistantResponse,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        List<Message> saved = chatService.saveChat(chatId, userMessage, assistantResponse, files);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable String chatId) {
        List<Message> history = chatService.getChatHistory(chatId);
        return ResponseEntity.ok(history);
    }


}