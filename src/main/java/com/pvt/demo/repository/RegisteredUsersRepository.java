package com.pvt.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.RegisteredUsers;
import java.util.List;

public interface RegisteredUsersRepository extends JpaRepository<RegisteredUsers, Long> {
    List<RegisteredUsers> findByEventInstanceId(Long eventInstanceId);
    List<RegisteredUsers> findByUserId(Long userId);
    RegisteredUsers findByUserIdAndEventInstanceId(Long userId, Long eventInstanceId);
}