package com.junior.cadastro.DTO;

import java.util.List;
import java.util.Map;

public record PluggyWebhookEvent(
        String event,
        String eventId,
        String itemId,
        String clientUserId,
        String triggeredBy,
        String accountId,
        List<String> transactionIds,
        String createdTransactionsLink,
        Map<String, Object> error,
        Map<String, Object> data
) {
}