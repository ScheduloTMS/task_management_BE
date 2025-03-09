package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByUserId(String userId);

    @Query("SELECT MAX(u.userId) FROM Users u WHERE u.userId LIKE ?1%")
    String findLastUserIdByRole(String rolePrefix);


    List<Users> findAllByDeletedAtIsNull();


    Optional<Users> findByUserIdAndDeletedAtIsNull(String userId);
}