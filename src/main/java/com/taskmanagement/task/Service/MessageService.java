package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Entity.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

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
        try {
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
                    .delivered(false) // Initially not delivered
                    .build();
            messageRepository.save(message);

            // Send the message to the recipient via WebSocket
            String destination = "/user/" + messageDTO.getReceiverId() + "/queue/messages";
            messagingTemplate.convertAndSend(destination, MessageDTO.builder()
                    .msgId(message.getMsgId())
                    .senderId(sender.getUserId())
                    .receiverId(receiver.getUserId())
                    .content(message.getContent())
                    .attachment(message.getAttachment())
                    .delivered(false) // Initially not delivered
                    .read(false) // Initially not read
                    .sendAt(message.getSendAt())
                    .build());

            // Mark as delivered after sending
            message.setDelivered(true);
            messageRepository.save(message);

            // Return the saved message as a DTO
            return MessageDTO.builder()
                    .msgId(message.getMsgId())
                    .senderId(sender.getUserId())
                    .receiverId(receiver.getUserId())
                    .content(message.getContent())
                    .attachment(message.getAttachment())
                    .delivered(message.isDelivered())
                    .read(message.isRead())
                    .sendAt(message.getSendAt())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    public void markAsRead(UUID messageId) {
        try {
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));
            message.setRead(true); // Mark as read
            messageRepository.save(message);

            // Notify the sender that the message has been read
            String destination = "/user/" + message.getSender().getUserId() + "/queue/messages";
            messagingTemplate.convertAndSend(destination, MessageDTO.builder()
                    .msgId(message.getMsgId())
                    .senderId(message.getSender().getUserId())
                    .receiverId(message.getReceiver().getUserId())
                    .content(message.getContent())
                    .delivered(message.isDelivered())
                    .read(message.isRead()) // Include updated read status
                    .sendAt(message.getSendAt())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark message as read: " + e.getMessage());
        }
    }
}