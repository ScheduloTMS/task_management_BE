package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        Users sender = userRepository.findByEmail(messageDTO.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Users receiver = userRepository.findByEmail(messageDTO.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setContent(messageDTO.getContent());
        messageEntity.setSendAt(LocalDateTime.now());
        messageEntity.setRead(false);

        messageRepository.save(messageEntity);


        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/messages",
                messageDTO
        );
    }


    @GetMapping("/history")
    public List<MessageDTO> getChatHistory(
            @RequestParam String senderEmail,
            @RequestParam String receiverEmail
    ) {
        List<MessageEntity> messages = messageRepository.findMessagesBetweenUsers(senderEmail, receiverEmail);

        return messages.stream().map(message -> MessageDTO.builder()
                .senderEmail(message.getSender().getEmail())
                .receiverEmail(message.getReceiver().getEmail())
                .content(message.getContent())
                .sendAt(message.getSendAt())
                .read(message.isRead())
                .build()
        ).collect(Collectors.toList());
    }


}
