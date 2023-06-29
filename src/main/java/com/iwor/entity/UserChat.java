package com.iwor.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Instant;

@Data
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users_chat", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chat_id"}))
public class UserChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 128)
    private String createdBy;

    public UserChat(Long id, @NonNull User user, @NonNull Chat chat, Instant createdAt, String createdBy) {
        this.id = id;
        this.user = user;
        this.chat = chat;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.user.getUserChats().add(this);
        this.chat.getUserChats().add(this);
    }

    public void setUser(User user) {
        user.getUserChats().add(this);
        this.user = user;
    }

    public void setChat(Chat chat) {
        chat.getUserChats().add(this);
        this.chat = chat;
    }
}
