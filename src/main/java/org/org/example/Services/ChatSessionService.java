package org.example.Services;

import org.example.Models.Chat;
import org.example.Repositorios.ChatRepository;
import org.example.Repositorios.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatSessionService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;

    public List<Chat> getChatsByUser(String username) {
        return chatRepository.findByUsernameOrderByLastUpdatedDesc(username);
    }

    public Chat createOrUpdateChat(Chat chat) {
        Chat existing = chatRepository.findByChatId(chat.getChatId());
        if (existing != null) {
            existing.setLastUpdated(LocalDateTime.now());
            if (chat.getTitle() != null && !chat.getTitle().isBlank()) {
                existing.setTitle(chat.getTitle());
            }
            return chatRepository.save(existing);
        }
        chat.setLastUpdated(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    public Chat getChatById(String chatId) {
        return chatRepository.findByChatId(chatId);
    }

    public void deleteChat(String chatId) {
        Chat existing = chatRepository.findByChatId(chatId);
        if (existing != null) {
            chatRepository.delete(existing);
        }
    }
}
