# RAG Ingestion Strategy

## Overview

Every ticket is converted to a single `Document` (Spring AI) and stored in the PGVector store. Ingestion happens synchronously after each create or update operation in `TicketService`.

## Document Structure

### Content (text field)

The document content is built by `Ticket.toDocumentText()`:

```
Title: <title>
Description: <description>
Status: <status>
Priority: <priority>
Assignee: <assignee>
Category: <category>
```

Example:
```
Title: Login page broken on Safari
Description: Users on Safari 17 cannot log in — the submit button is unresponsive.
Status: IN_PROGRESS
Priority: HIGH
Assignee: alice
Category: authentication
```

### Metadata (JSONB)

| Key | Type | Purpose |
|-----|------|---------|
| ticketId | String | Identifies the source ticket for citation and deletion |
| status | String | For future filtered retrieval |
| priority | String | For future filtered retrieval |
| assignee | String | For future filtered retrieval |
| category | String | For future filtered retrieval |

## Chunking Rationale

One document per ticket. No sub-chunking.

Justification:
- Ticket descriptions average 50–300 tokens — well below the 8192-token limit of text-embedding-3-small.
- Splitting ticket fields into separate chunks would destroy cross-field semantic context (e.g. "authentication bug assigned to alice" requires title + category + assignee to be co-embedded).
- Fixed-size chunking would split mid-sentence on longer descriptions with no benefit.

## Ingestion Lifecycle

```
1. Ticket created or updated
2. RagIngestionService.ingest(ticket) called
3. Delete existing vector document(s) where metadata.ticketId = ticket.id
4. Build Document with content = ticket.toDocumentText(), metadata = {...}
5. Embed content via text-embedding-3-small (1536 dims)
6. Store embedding in PGVector vector_store table
```

## Re-ingestion Triggers

| Event | Action |
|-------|--------|
| POST /api/tickets | Ingest new document |
| PATCH /api/tickets/{id} | Delete old + ingest updated |
| PATCH /api/tickets/{id}/status | Delete old + ingest updated (status change reflected in embedding) |

## Configuration

```yaml
rag:
  retrieval:
    top-k: 5
    similarity-threshold: 0.7
```

Both values are runtime-configurable via environment variables or application.yml overrides.
