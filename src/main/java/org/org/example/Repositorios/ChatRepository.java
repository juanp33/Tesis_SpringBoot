package org.example.Repositorios;

import org.example.Models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUsernameOrderByLastUpdatedDesc(String username);
    Chat findByChatId(String chatId);
}