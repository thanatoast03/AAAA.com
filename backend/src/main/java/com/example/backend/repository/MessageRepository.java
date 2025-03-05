package com.example.backend.repository;

import com.example.backend.DTO.MessageDTO;
import com.example.backend.model.Account;
import com.example.backend.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.security.Principal;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    void deleteById(Long id);

    @Query("""
        SELECT m.id AS id, m.text AS text, m.time AS time, a.username AS name
        FROM Message m 
        JOIN m.sender a    
        ORDER BY m.id ASC
    """)
    List<MessageDTO> findLast100Messages(Pageable pageable); // get last 100 messages

    @Query("""
        SELECT m.id AS id, m.text AS text, m.time AS time, a.username AS name
        FROM Message m
        JOIN m.sender a
        WHERE m.id < :messageId
        ORDER BY m.id ASC
    """)
    List<MessageDTO> findTop100BeforeMessageId(@Param("messageId") Long messageId, Pageable pageable);
}
