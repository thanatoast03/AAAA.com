package com.example.backend.repository;

import com.example.backend.model.Account;
import com.example.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.Principal;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender(Account sender);
}
