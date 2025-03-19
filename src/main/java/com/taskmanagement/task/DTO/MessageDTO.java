package com.taskmanagement.task.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private UUID msgId; // Unique identifier for the message
    private String senderId; // ID of the sender
    private String receiverId; // ID of the receiver
    private String content; // Content of the message
    private Long attachment; // Optional attachment (e.g., file ID)
    private LocalDateTime sendAt; // Timestamp when the message was sent
}