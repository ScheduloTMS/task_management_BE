package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Entity.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // Fetch sender and receiver from the database
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Create and save the message
        MessageEntity message = MessageEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .content(messageDTO.getContent())
                .attachment(messageDTO.getAttachment())
                .sendAt(LocalDateTime.now())
                .read(false)
                .build();
        messageRepository.save(message);

        // Send the message to the recipient via WebSocket
        String destination = "/user/" + messageDTO.getReceiverId() + "/queue/messages";
        messagingTemplate.convertAndSend(destination, messageDTO);

        // Return the saved message as a DTO
        return MessageDTO.builder()
                .msgId(message.getMsgId())
                .senderId(sender.getUserId())
                .receiverId(receiver.getUserId())
                .content(message.getContent())
                .attachment(message.getAttachment())
                .sendAt(message.getSendAt())
                .read(false)
                .build();
    }
}