package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID msgId;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", nullable = false)
    private Users receiver;

    @Column(nullable = false)
    private String content;

    @Column(name = "attachment")
    private Long attachment;

    @Column(nullable = false)
    private boolean read = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sendAt = LocalDateTime.now();



    // No-args constructor
    public MessageEntity() {}

    // All-args constructor
    public MessageEntity(UUID msgId, Users sender, Users receiver, String content, Long attachment, boolean read, LocalDateTime sendAt) {
        this.msgId = msgId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.attachment = attachment;
        this.read = read;
        this.sendAt = sendAt;
    }

    // Getters and Setters
    public UUID getMsgId() {
        return msgId;
    }

    public void setMsgId(UUID msgId) {
        this.msgId = msgId;
    }

    public Users getSender() {
        return sender;
    }

    public void setSender(Users sender) {
        this.sender = sender;
    }

    public Users getReceiver() {
        return receiver;
    }

    public void setReceiver(Users receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAttachment() {
        return attachment;
    }

    public void setAttachment(Long attachment) {
        this.attachment = attachment;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }

    // Builder pattern
    public static class Builder {
        private UUID msgId;
        private Users sender;
        private Users receiver;
        private String content;
        private Long attachment;
        private boolean read = false;
        private LocalDateTime sendAt = LocalDateTime.now();

        public Builder msgId(UUID msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder sender(Users sender) {
            this.sender = sender;
            return this;
        }

        public Builder receiver(Users receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder attachment(Long attachment) {
            this.attachment = attachment;
            return this;
        }

        public Builder read(boolean read) {
            this.read = read;
            return this;
        }

        public Builder sendAt(LocalDateTime sendAt) {
            this.sendAt = sendAt;
            return this;
        }

        public MessageEntity build() {
            return new MessageEntity(msgId, sender, receiver, content, attachment, read, sendAt);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
