package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID msgId;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "userId", nullable = false)
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "userId", nullable = false)
    private Users receiver;

    @Column(nullable = false)
    private String content;

    @Column(name = "attachment")
    private Long attachment;

    @Column(nullable = false)
    private boolean read = false;

    @Column(nullable = false)
    private boolean delivered = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sendAt = LocalDateTime.now();
}
