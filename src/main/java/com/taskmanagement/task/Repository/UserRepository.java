package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    @Query("SELECT u FROM Users u WHERE u.userId = :userId")
    Optional<Users> findByUserId(@Param("userId") String userId);


    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.photo WHERE u.userId = :userId")
    Optional<Users> findByIdWithPhoto(@Param("userId") String userId);


    @Query("SELECT u.userId FROM Users u WHERE u.userId LIKE :rolePrefix% ORDER BY u.userId DESC LIMIT 1")
    String findLastUserIdByRole(@Param("rolePrefix") String rolePrefix);


    List<Users> findAllByDeletedAtIsNull();


    Optional<Users> findByUserIdAndDeletedAtIsNull(String userId);
}