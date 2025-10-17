package org.example.Services;

import org.example.Models.Message;
import org.example.Repositorios.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    private final Path uploadDir = Paths.get("uploads");

    public ChatService() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }
    public List<Message> getChatHistory(String chatId) {
        return messageRepository.findByChatIdOrderByIdAsc(chatId);
    }
    public List<Message> saveChat(String chatId, String userMessage, String assistantResponse, List<MultipartFile> files) throws IOException {
        List<Message> saved = new ArrayList<>();

        // Guardar archivos del usuario (si hay)
        List<String> filePaths = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Path path = uploadDir.resolve(file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                filePaths.add(path.toString());
            }
        }

        // Guardar mensaje del usuario
        Message userMsg = new Message(chatId, "user", userMessage,
                filePaths.isEmpty() ? null : Path.of(filePaths.get(0)).getFileName().toString(),
                filePaths.isEmpty() ? null : filePaths.get(0));
        saved.add(messageRepository.save(userMsg));

        // Guardar respuesta del asistente
        Message aiMsg = new Message(chatId, "assistant", assistantResponse, null, null);
        saved.add(messageRepository.save(aiMsg));

        return saved;
    }


}
