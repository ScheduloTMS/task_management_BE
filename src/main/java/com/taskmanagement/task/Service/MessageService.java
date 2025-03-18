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

            User sender = userRepository.findById(messageDTO.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            User receiver = userRepository.findById(messageDTO.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));


            MessageEntity message = MessageEntity.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content(messageDTO.getContent())
                    .attachment(messageDTO.getAttachment())
                    .sendAt(LocalDateTime.now())
                    .read(false)
                    .delivered(false)
                    .build();
            messageRepository.save(message);


            String destination = "/user/" + messageDTO.getReceiverId() + "/queue/messages";
            messagingTemplate.convertAndSend(destination, MessageDTO.builder()
                    .msgId(message.getMsgId())
                    .senderId(sender.getUserId())
                    .receiverId(receiver.getUserId())
                    .content(message.getContent())
                    .attachment(message.getAttachment())
                    .delivered(false)
                    .read(false)
                    .sendAt(message.getSendAt())
                    .build());


            message.setDelivered(true);
            messageRepository.save(message);


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
            message.setRead(true);
            messageRepository.save(message);


            String destination = "/user/" + message.getSender().getUserId() + "/queue/messages";
            messagingTemplate.convertAndSend(destination, MessageDTO.builder()
                    .msgId(message.getMsgId())
                    .senderId(message.getSender().getUserId())
                    .receiverId(message.getReceiver().getUserId())
                    .content(message.getContent())
                    .delivered(message.isDelivered())
                    .read(message.isRead())
                    .sendAt(message.getSendAt())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark message as read: " + e.getMessage());
        }
    }
}