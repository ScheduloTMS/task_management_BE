package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // ✅ Send message (used in WebSocket and future REST if needed)
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Users sender = userRepository.findByEmail(messageDTO.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Users receiver = userRepository.findByEmail(messageDTO.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        MessageEntity message = MessageEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .content(messageDTO.getContent())
                .attachment(messageDTO.getAttachment())
                .sendAt(LocalDateTime.now())
                .read(false)
                .build();

        messageRepository.save(message);

        return MessageDTO.builder()
                .msgId(message.getMsgId())
                .senderEmail(sender.getEmail())
                .receiverEmail(receiver.getEmail())
                .content(message.getContent())
                .attachment(message.getAttachment())
                .sendAt(message.getSendAt())
                .read(message.isRead())
                .build();
    }


    public List<MessageDTO> getChatHistory(String senderEmail, String receiverEmail) {
        List<MessageEntity> messages = messageRepository.findMessagesBetweenUsers(senderEmail, receiverEmail);

        return messages.stream().map(message -> MessageDTO.builder()
                .senderEmail(message.getSender().getEmail())
                .receiverEmail(message.getReceiver().getEmail())
                .content(message.getContent())
                .attachment(message.getAttachment())
                .sendAt(message.getSendAt())
                .read(message.isRead())
                .build()
        ).collect(Collectors.toList());
    }
}
