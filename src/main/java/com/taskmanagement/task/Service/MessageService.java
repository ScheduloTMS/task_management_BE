package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Entity.User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public MessageDTO sendMessage(MessageDTO messageDTO) {

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
                .build();

        messageRepository.save(message);

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
