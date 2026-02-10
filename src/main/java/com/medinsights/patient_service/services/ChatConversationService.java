package com.medinsights.patient_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medinsights.patient_service.dto.chatconversation.AddMessageRequest;
import com.medinsights.patient_service.dto.chatconversation.ChatConversationResponse;
import com.medinsights.patient_service.dto.chatconversation.CreateConversationRequest;
import com.medinsights.patient_service.entities.ChatConversation;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.repositories.ChatConversationRepository;
import com.medinsights.patient_service.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing chat conversations
 * Handles conversation lifecycle, message management, and history
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatConversationService {

    private final ChatConversationRepository conversationRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper;

    /**
     * Create new conversation for a patient
     */
    @Transactional
    public ChatConversationResponse createConversation(UUID patientId, CreateConversationRequest request) {
        log.info("Creating conversation for patient {} with session {}", patientId, request.getSessionId());

        // Validate patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        // Check if session ID already exists
        if (conversationRepository.existsBySessionId(request.getSessionId())) {
            throw new RuntimeException("Conversation with session ID already exists: " + request.getSessionId());
        }

        // Create conversation
        ChatConversation conversation = ChatConversation.builder()
                .patient(patient)
                .sessionId(request.getSessionId())
                .title(request.getTitle() != null ? request.getTitle() : "Nouvelle conversation")
                .messages("[]")  // Empty JSON array
                .messageCount(0)
                .startedAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .status("ACTIVE")
                .tags(request.getTags())
                .build();

        ChatConversation saved = conversationRepository.save(conversation);
        log.info("✅ Conversation created: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Get conversation by ID
     */
    @Transactional(readOnly = true)
    public ChatConversationResponse getConversation(UUID conversationId) {
        log.info("Fetching conversation: {}", conversationId);
        
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));
        
        return toResponse(conversation);
    }

    /**
     * Get conversation by session ID
     */
    @Transactional(readOnly = true)
    public ChatConversationResponse getConversationBySession(String sessionId) {
        log.info("Fetching conversation by session: {}", sessionId);
        
        ChatConversation conversation = conversationRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Conversation not found for session: " + sessionId));
        
        return toResponse(conversation);
    }

    /**
     * Get all conversations for a patient
     */
    @Transactional(readOnly = true)
    public List<ChatConversationResponse> getPatientConversations(UUID patientId, String status) {
        log.info("Fetching conversations for patient {} with status {}", patientId, status);

        List<ChatConversation> conversations;
        if (status != null && !status.isEmpty()) {
            conversations = conversationRepository.findByPatientIdAndStatusOrderByLastMessageAtDesc(patientId, status);
        } else {
            conversations = conversationRepository.findByPatientIdOrderByLastMessageAtDesc(patientId);
        }

        return conversations.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Add message to conversation
     */
    @Transactional
    public ChatConversationResponse addMessage(UUID conversationId, AddMessageRequest request) {
        log.info("Adding message to conversation {} with role {}", conversationId, request.getRole());

        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        // Parse existing messages
        List<Map<String, Object>> messages = parseMessages(conversation.getMessages());

        // Add new message
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("role", request.getRole());
        newMessage.put("content", request.getContent());
        newMessage.put("timestamp", LocalDateTime.now().toString());
        messages.add(newMessage);

        // Update conversation
        conversation.setMessages(serializeMessages(messages));
        conversation.setMessageCount(messages.size());
        conversation.setLastMessageAt(LocalDateTime.now());

        // Auto-generate title from first user message if not set
        if ("Nouvelle conversation".equals(conversation.getTitle()) && "user".equals(request.getRole())) {
            String title = request.getContent().length() > 50 
                ? request.getContent().substring(0, 50) + "..." 
                : request.getContent();
            conversation.setTitle(title);
        }

        ChatConversation saved = conversationRepository.save(conversation);
        log.info("✅ Message added to conversation {}", conversationId);

        return toResponse(saved);
    }

    /**
     * Archive conversation
     */
    @Transactional
    public ChatConversationResponse archiveConversation(UUID conversationId) {
        log.info("Archiving conversation: {}", conversationId);

        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.setStatus("ARCHIVED");
        ChatConversation saved = conversationRepository.save(conversation);

        log.info("✅ Conversation archived: {}", conversationId);
        return toResponse(saved);
    }

    /**
     * Delete conversation
     */
    @Transactional
    public void deleteConversation(UUID conversationId) {
        log.info("Deleting conversation: {}", conversationId);

        if (!conversationRepository.existsById(conversationId)) {
            throw new RuntimeException("Conversation not found: " + conversationId);
        }

        conversationRepository.deleteById(conversationId);
        log.info("✅ Conversation deleted: {}", conversationId);
    }

    /**
     * Count conversations for patient
     */
    @Transactional(readOnly = true)
    public Long countConversations(UUID patientId, String status) {
        if (status != null && !status.isEmpty()) {
            return conversationRepository.countByPatientIdAndStatus(patientId, status);
        }
        return conversationRepository.countByPatientId(patientId);
    }

    // ========== Helper Methods ==========

    private ChatConversationResponse toResponse(ChatConversation conversation) {
        return ChatConversationResponse.builder()
                .id(conversation.getId())
                .patientId(conversation.getPatient().getId())
                .sessionId(conversation.getSessionId())
                .title(conversation.getTitle())
                .messages(conversation.getMessages())
                .messageCount(conversation.getMessageCount())
                .startedAt(conversation.getStartedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .status(conversation.getStatus())
                .tags(conversation.getTags())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseMessages(String messagesJson) {
        try {
            if (messagesJson == null || messagesJson.trim().isEmpty() || "[]".equals(messagesJson.trim())) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(messagesJson, List.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse messages JSON: {}", messagesJson, e);
            return new ArrayList<>();
        }
    }

    private String serializeMessages(List<Map<String, Object>> messages) {
        try {
            return objectMapper.writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize messages", e);
            return "[]";
        }
    }
}
