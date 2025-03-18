package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.MessageDTO;
import com.taskmanagement.task.Service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        messageService.sendMessage(messageDTO);
    }
}