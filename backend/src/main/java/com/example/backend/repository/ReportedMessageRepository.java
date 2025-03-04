package com.example.backend.repository;

import com.example.backend.model.Account;
import com.example.backend.model.Message;
import com.example.backend.model.ReportedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportedMessageRepository extends JpaRepository<ReportedMessage, Long> {
    List<ReportedMessage> findByCreator(Account creator);
    boolean existsByReporterAndMessage(Account reporter, Message message);
}
