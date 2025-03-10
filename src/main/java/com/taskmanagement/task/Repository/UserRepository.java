package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByIdWithPhoto(@Param("userId") String userId);


    List<User> findAllByDeletedAtIsNull();


    Optional<User> findByUserIdAndDeletedAtIsNull(String userId);
}
