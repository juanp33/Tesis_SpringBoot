package org.example.Controllers;

import org.example.Configuracion.JwtUtil;
import org.example.Models.Chat;
import org.example.Models.Message;
import org.example.Repositorios.MessageRepository;
import org.example.Services.ChatService;
import org.example.Services.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatSessionService chatService;
    @Autowired
    private ChatService chatService2;
    @Autowired
    private MessageRepository MessageRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;


    @GetMapping("/find/{chatId}")
    public Chat getChatById(@PathVariable String chatId) {
        return chatService.getChatById(chatId);
    }


    @DeleteMapping("/{chatId}")
    public void deleteChat(@PathVariable String chatId) {
        chatService.deleteChat(chatId);
    }


    @GetMapping("/mis-chats")
    public ResponseEntity<?> getChatsByUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no válido o faltante");
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty() || "null".equalsIgnoreCase(token)) {
            return ResponseEntity.badRequest().body("Token vacío o inválido");
        }

        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token malformado: " + e.getMessage());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.isTokenValid(token, userDetails)) {
            return ResponseEntity.status(401).body("Token no válido o expirado");
        }

        List<Chat> chats = chatService.getChatsByUser(username);
        return ResponseEntity.ok(chats);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createChat(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Chat chat) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no válido o faltante");
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty() || "null".equalsIgnoreCase(token)) {
            return ResponseEntity.badRequest().body("Token vacío o inválido");
        }

        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token malformado: " + e.getMessage());
        }

        chat.setUsername(username);
        Chat saved = chatService.createOrUpdateChat(chat);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getMessagesByChat(@PathVariable String chatId) {
        List<Message> messages = chatService2.getChatHistory(chatId);
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messages);
    }


}
