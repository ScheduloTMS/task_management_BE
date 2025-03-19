package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Entity.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

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

    /**
     * Sends a message and broadcasts it to both the sender and receiver.
     */
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        try {
            // Validate sender and receiver
            User sender = userRepository.findById(messageDTO.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            User receiver = userRepository.findById(messageDTO.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Save the message to the database
            MessageEntity message = MessageEntity.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content(messageDTO.getContent())
                    .attachment(messageDTO.getAttachment())
                    .sendAt(LocalDateTime.now())
                    .build();
            messageRepository.save(message);

            // Convert the saved entity to DTO for broadcasting
            MessageDTO savedMessageDTO = convertToDTO(message);

            // Broadcast the message to the receiver
            String receiverDestination = "/user/" + receiver.getUserId() + "/queue/messages";
            messagingTemplate.convertAndSend(receiverDestination, savedMessageDTO);

            // Broadcast the message to the sender
            String senderDestination = "/user/" + sender.getUserId() + "/queue/messages";
            messagingTemplate.convertAndSend(senderDestination, savedMessageDTO);

            return savedMessageDTO;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Fetches messages between two users.
     */
    public List<MessageDTO> getMessagesBetweenUsers(String senderId, String receiverId) {
        try {
            // Fetch messages where the sender and receiver are involved
            List<MessageEntity> messages = messageRepository.findBySenderUserIdAndReceiverUserId(senderId, receiverId);
            messages.addAll(messageRepository.findBySenderUserIdAndReceiverUserId(receiverId, senderId));

            // Sort messages by timestamp (oldest first)
            messages.sort((m1, m2) -> m1.getSendAt().compareTo(m2.getSendAt()));

            // Convert entities to DTOs
            return messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch messages: " + e.getMessage());
        }
    }

    /**
     * Converts a MessageEntity to a MessageDTO.
     */
    private MessageDTO convertToDTO(MessageEntity message) {
        return MessageDTO.builder()
                .msgId(message.getMsgId())
                .senderId(message.getSender().getUserId())
                .receiverId(message.getReceiver().getUserId())
                .content(message.getContent())
                .attachment(message.getAttachment())
                .sendAt(message.getSendAt())
                .build();
    }
}