# Prompt History

Record of significant prompts used during development, AI responses, and mistakes found.

---

## Entry Template

```
## [DATE] — [COMPONENT]

### Prompt
<what was asked of the AI>

### AI Response Summary
<what the AI generated / proposed>

### Mistakes Found
<bugs, wrong API usage, missing validation, etc.>

### Fix Applied
<what was changed and why>
```

---

## 2024-01-15 — Initial Project Setup

### Prompt
Generate a Spring Boot 3.x + Spring AI project skeleton with PGVector integration.

### AI Response Summary
Generated pom.xml with Spring AI dependencies and basic application.yml.

### Mistakes Found
- Used `spring-ai-openai` without BOM — version conflicts with Spring Boot managed deps.
- Set `dimensions: 768` (wrong for text-embedding-3-small which needs 1536).

### Fix Applied
- Added `spring-ai-bom` to `<dependencyManagement>`.
- Corrected dimensions to 1536.

---

## 2024-01-15 — RagIngestionService

### Prompt
Implement RagIngestionService that deletes and re-inserts a ticket's vector document.

### AI Response Summary
Generated service using `vectorStore.delete(List.of(documentId))` — attempted to delete by Spring AI document UUID.

### Mistakes Found
- Wrong deletion approach: we don't store the Spring AI UUID — we store our own ticketId in metadata. Deletion must use metadata filter, not document UUID.
- Used field injection `@Autowired` instead of constructor injection.

### Fix Applied
- Switched to `FilterExpressionBuilder` with `eq("ticketId", ...)` for deletion.
- Converted to `@RequiredArgsConstructor` + final fields.
