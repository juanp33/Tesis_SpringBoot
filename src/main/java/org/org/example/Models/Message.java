package org.example.Models;

import jakarta.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String role;      // user / assistant
    @Column(columnDefinition = "TEXT")
    private String content;
    private String filename;
    private String filepath;
    @ManyToOne
    @JoinColumn(name = "chat_id_fk")
    private Chat chat;
    public Message() {}

    public Message(String chatId, String role, String content, String filename, String filepath) {
        this.chatId = chatId;
        this.role = role;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }
}