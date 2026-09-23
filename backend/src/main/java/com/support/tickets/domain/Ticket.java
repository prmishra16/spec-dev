package com.support.tickets.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Priority priority = Priority.MEDIUM;

    @Column(length = 255)
    private String assignee;

    @Column(length = 255)
    private String category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Produces a human-readable text representation of this ticket for RAG ingestion.
     * All key fields are included so semantic search can match on any of them.
     */
    public String toDocumentText() {
        return "Title: " + title + "\n" +
               "Description: " + description + "\n" +
               "Status: " + (status != null ? status.name() : "OPEN") + "\n" +
               "Priority: " + (priority != null ? priority.name() : "MEDIUM") + "\n" +
               "Assignee: " + (assignee != null ? assignee : "Unassigned") + "\n" +
               "Category: " + (category != null ? category : "General");
    }
}
