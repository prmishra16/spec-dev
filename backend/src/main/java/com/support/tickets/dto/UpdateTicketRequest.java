package com.support.tickets.dto;

import com.support.tickets.domain.Priority;

public record UpdateTicketRequest(
        String title,
        String description,
        Priority priority,
        String assignee,
        String category
) {}
