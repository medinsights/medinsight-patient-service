package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ChatConversation entity
 * Provides database access methods for conversation history
 */
@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, UUID> {

    /**
     * Find conversation by session ID
     * Used by chatbot to continue existing conversation
     */
    Optional<ChatConversation> findBySessionId(String sessionId);

    /**
     * Find all conversations for a patient, ordered by most recent
     */
    List<ChatConversation> findByPatientIdOrderByLastMessageAtDesc(UUID patientId);

    /**
     * Find conversations by patient and status
     */
    List<ChatConversation> findByPatientIdAndStatusOrderByLastMessageAtDesc(UUID patientId, String status);

    /**
     * Find active conversations for a patient
     */
    List<ChatConversation> findByPatientIdAndStatus(UUID patientId, String status);

    /**
     * Count conversations for a patient
     */
    Long countByPatientId(UUID patientId);

    /**
     * Count conversations by status
     */
    Long countByPatientIdAndStatus(UUID patientId, String status);

    /**
     * Check if session ID exists
     */
    boolean existsBySessionId(String sessionId);
}
