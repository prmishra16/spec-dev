# Test Strategy

## Testing Pyramid

```
         /\
        /  \         E2E (manual / Playwright — out of scope v1)
       /────\
      /      \       Integration: @SpringBootTest + MockMvc
     /────────\
    /          \     Unit: JUnit 5 + Mockito (StateMachineService, pure logic)
   /────────────\
```

## Unit Tests

### StateMachineServiceTest

Exhaustively cover all 5×5 transition matrix entries:

- **Valid transitions (5):** OPEN→IN_PROGRESS, OPEN→CANCELLED, IN_PROGRESS→RESOLVED, IN_PROGRESS→CANCELLED, RESOLVED→CLOSED → assert no exception thrown.
- **Invalid transitions (all others):** assert `InvalidStateTransitionException` is thrown.

Total: 5 valid + ~15 meaningful invalid = ~20 test methods.

### AiAssistantServiceTest

- Mock VectorStore: return empty list → assert `noRelevantTickets=true`, no LLM call.
- Mock VectorStore: return 2 documents → assert `noRelevantTickets=false`, LLM called once.

## Integration Tests

### TicketControllerTest (@SpringBootTest + MockMvc)

| Test | Endpoint | Expected |
|------|----------|---------|
| createTicket_valid | POST /api/tickets | 201 + body |
| createTicket_missingTitle | POST /api/tickets | 400 + errors.title |
| createTicket_missingDesc | POST /api/tickets | 400 + errors.description |
| getTicket_found | GET /api/tickets/{id} | 200 |
| getTicket_notFound | GET /api/tickets/9999 | 404 |
| transitionStatus_valid | PATCH /api/tickets/{id}/status | 200 |
| transitionStatus_invalid | PATCH /api/tickets/{id}/status | 409 |
| addComment_valid | POST /api/tickets/{id}/comments | 201 |

### RagIngestionIT (manual / future)

Verify that after creating a ticket, `vectorStore.similaritySearch("ticket title keywords")` returns the ticket.

## Test Configuration

- Use H2 in-memory DB for unit/integration tests if PGVector unavailable (or use Testcontainers with postgres+pgvector image).
- Mock `OpenAiChatModel` and `VectorStore` in unit tests.
- Use `@MockBean` for AI services in controller tests.

## Coverage Target

- StateMachineService: 100% branch coverage.
- TicketService: ≥ 80% line coverage.
- Controllers: all happy paths + key error paths.
