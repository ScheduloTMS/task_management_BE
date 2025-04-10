package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(m.sender.email = :email1 AND m.receiver.email = :email2) OR " +
            "(m.sender.email = :email2 AND m.receiver.email = :email1) " +
            "ORDER BY m.sendAt ASC")
    List<MessageEntity> findMessagesBetweenUsers(
            @Param("email1") String email1,
            @Param("email2") String email2
    );


}
