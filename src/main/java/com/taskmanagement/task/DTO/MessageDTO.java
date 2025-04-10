package com.taskmanagement.task.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDTO {
    private UUID msgId;
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private Long attachment;
    private LocalDateTime sendAt;
    private boolean read;


    public MessageDTO() {}


    public MessageDTO(UUID msgId, String senderEmail, String receiverEmail, String content,
                      Long attachment, LocalDateTime sendAt, boolean read) {
        this.msgId = msgId;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.content = content;
        this.attachment = attachment;
        this.sendAt = sendAt;
        this.read = read;
    }

    // Getters and Setters
    public UUID getMsgId() {
        return msgId;
    }

    public void setMsgId(UUID msgId) {
        this.msgId = msgId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    public static class Builder {
        private UUID msgId;
        private String senderEmail;
        private String receiverEmail;
        private String content;
        private Long attachment;
        private LocalDateTime sendAt;
        private boolean read;

        public Builder msgId(UUID msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder senderEmail(String senderEmail) {
            this.senderEmail = senderEmail;
            return this;
        }

        public Builder receiverEmail(String receiverEmail) {
            this.receiverEmail = receiverEmail;
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

        public Builder sendAt(LocalDateTime sendAt) {
            this.sendAt = sendAt;
            return this;
        }

        public Builder read(boolean read) {
            this.read = read;
            return this;
        }

        public MessageDTO build() {
            return new MessageDTO(msgId, senderEmail, receiverEmail, content, attachment, sendAt, read);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
