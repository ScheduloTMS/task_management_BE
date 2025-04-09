package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Service.MessageService;
import com.taskmanagement.task.Service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        messageService.sendMessage(messageDTO);
    }

    @GetMapping("/api/messages")
    public List<MessageDTO> getMessagesBetweenUsers(
            @RequestParam String receiverId,

            @AuthenticationPrincipal UserDetails userDetails) {


        String senderId = userService.getUserByEmail(userDetails.getUsername()).getUserId();

        return messageService.getMessagesBetweenUsers(senderId, receiverId);
    }
}


