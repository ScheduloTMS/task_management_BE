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
    private UUID msgId;
    private String senderId; // Ensure this is String
    private String receiverId; // Ensure this is String
    private String content;
    private Long attachment;
    private LocalDateTime sendAt;
    private boolean read;
    private boolean delivered;
}