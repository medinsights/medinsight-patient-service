package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.chatconversation.AddMessageRequest;
import com.medinsights.patient_service.dto.chatconversation.ChatConversationResponse;
import com.medinsights.patient_service.dto.chatconversation.CreateConversationRequest;
import com.medinsights.patient_service.services.ChatConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Chat Conversation Management
 * Supports chatbot conversation history and context management
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Conversation Management", description = "APIs for managing chatbot conversation history")
public class ChatConversationController {

    private final ChatConversationService conversationService;

    @PostMapping("/patients/{patientId}")
    @Operation(
            summary = "Create new conversation",
            description = "Create a new chat conversation for a patient with a unique session ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Conversation created successfully",
                    content = @Content(schema = @Schema(implementation = ChatConversationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data or duplicate session ID"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ChatConversationResponse> createConversation(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Valid @RequestBody CreateConversationRequest request
    ) {
        log.info("POST /api/conversations/patients/{} - Creating conversation", patientId);
        ChatConversationResponse response = conversationService.createConversation(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{conversationId}")
    @Operation(
            summary = "Get conversation by ID",
            description = "Retrieve conversation details including full message history"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversation found",
                    content = @Content(schema = @Schema(implementation = ChatConversationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ChatConversationResponse> getConversation(
            @Parameter(description = "Conversation UUID") @PathVariable UUID conversationId
    ) {
        log.info("GET /api/conversations/{} - Fetching conversation", conversationId);
        ChatConversationResponse response = conversationService.getConversation(conversationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(
            summary = "Get conversation by session ID",
            description = "Retrieve conversation by its session ID (used by chatbot to continue existing conversation)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversation found",
                    content = @Content(schema = @Schema(implementation = ChatConversationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ChatConversationResponse> getConversationBySession(
            @Parameter(description = "Session ID") @PathVariable String sessionId
    ) {
        log.info("GET /api/conversations/session/{} - Fetching conversation by session", sessionId);
        ChatConversationResponse response = conversationService.getConversationBySession(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}")
    @Operation(
            summary = "Get all conversations for a patient",
            description = "Retrieve all conversation history for a specific patient, optionally filtered by status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ChatConversationResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ChatConversationResponse>> getPatientConversations(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Filter by status (ACTIVE, ARCHIVED, DELETED)") 
            @RequestParam(required = false) String status
    ) {
        log.info("GET /api/conversations/patients/{} - Fetching conversations with status {}", patientId, status);
        List<ChatConversationResponse> conversations = conversationService.getPatientConversations(patientId, status);
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/{conversationId}/messages")
    @Operation(
            summary = "Add message to conversation",
            description = "Append a new message (user, assistant, or system) to an existing conversation"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message added successfully",
                    content = @Content(schema = @Schema(implementation = ChatConversationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ChatConversationResponse> addMessage(
            @Parameter(description = "Conversation UUID") @PathVariable UUID conversationId,
            @Valid @RequestBody AddMessageRequest request
    ) {
        log.info("POST /api/conversations/{}/messages - Adding {} message", conversationId, request.getRole());
        ChatConversationResponse response = conversationService.addMessage(conversationId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{conversationId}/archive")
    @Operation(
            summary = "Archive conversation",
            description = "Mark conversation as archived (not deleted, just hidden from active list)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversation archived successfully",
                    content = @Content(schema = @Schema(implementation = ChatConversationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ChatConversationResponse> archiveConversation(
            @Parameter(description = "Conversation UUID") @PathVariable UUID conversationId
    ) {
        log.info("PUT /api/conversations/{}/archive - Archiving conversation", conversationId);
        ChatConversationResponse response = conversationService.archiveConversation(conversationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{conversationId}")
    @Operation(
            summary = "Delete conversation",
            description = "Permanently delete a conversation and all its messages"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conversation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteConversation(
            @Parameter(description = "Conversation UUID") @PathVariable UUID conversationId
    ) {
        log.info("DELETE /api/conversations/{} - Deleting conversation", conversationId);
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patients/{patientId}/count")
    @Operation(
            summary = "Count conversations for patient",
            description = "Get total count of conversations for a patient, optionally filtered by status"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Long> countConversations(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status
    ) {
        log.info("GET /api/conversations/patients/{}/count - Counting conversations", patientId);
        Long count = conversationService.countConversations(patientId, status);
        return ResponseEntity.ok(count);
    }
}
