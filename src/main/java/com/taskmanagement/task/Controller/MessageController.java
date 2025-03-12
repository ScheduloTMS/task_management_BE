package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Entity.MessageEntity;
import com.taskmanagement.task.Entity.User;
import com.taskmanagement.task.Repository.MessageRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO messageDTO) {

        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));


        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setContent(messageDTO.getContent());
        messageEntity.setSendAt(LocalDateTime.now());
        messageRepository.save(messageEntity);
        System.out.println("📤 Sending message to /user/" + messageDTO.getReceiverId() + "/queue/messages");

        messagingTemplate.convertAndSendToUser(
                messageDTO.getReceiverId(),
                "/queue/messages",
                messageDTO
        );
        System.out.println("✅ Message successfully sent!");
        System.out.println("Message sent from " + sender.getUserId() + " to " + receiver.getUserId());
        System.out.println("Sending to: /user/" + messageDTO.getReceiverId() + "/queue/messages");
    }

}