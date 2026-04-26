package com.junior.cadastro.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.PluggyTransaction;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.PluggyIntegrationException;

//Converte o JSON da Pluggy em entidades de conta e transação.
@Component
public class PluggyMapper {

    public PluggyAccount toAccount(
            JsonNode accountNode,
            PluggyAccount account,
            PluggyItem item,
            User user
    ) {
        String pluggyAccountId = accountNode.path("id").asText(null);

        if (pluggyAccountId == null || pluggyAccountId.isBlank()) {
            throw new PluggyIntegrationException("Conta retornada pela Pluggy sem id.");
        }

        account.setPluggyAccountId(pluggyAccountId);
        account.setName(accountNode.path("name").asText(null));
        account.setType(accountNode.path("type").asText(null));
        account.setSubtype(accountNode.path("subtype").asText(null));
        account.setCurrencyCode(accountNode.path("currencyCode").asText(null));
        account.setBalance(readBalance(accountNode));
        account.setUpdatedAt(Instant.now());
        account.setItem(item);
        account.setUser(user);

        return account;
    }

    public PluggyTransaction toTransaction(
            JsonNode transactionNode,
            PluggyTransaction transaction,
            PluggyAccount account,
            User user
    ) {
        String pluggyTransactionId = transactionNode.path("id").asText(null);

        if (pluggyTransactionId == null || pluggyTransactionId.isBlank()) {
            throw new PluggyIntegrationException("Transação retornada pela Pluggy sem id.");
        }

        transaction.setPluggyTransactionId(pluggyTransactionId);
        transaction.setDate(readTransactionDate(transactionNode));
        transaction.setDescription(transactionNode.path("description").asText(null));
        transaction.setAmount(readAmount(transactionNode));
        transaction.setCurrencyCode(transactionNode.path("currencyCode").asText(null));
        transaction.setCategory(transactionNode.path("category").asText(null));
        transaction.setStatus(transactionNode.path("status").asText(null));
        transaction.setType(transactionNode.path("type").asText(null));
        transaction.setImportedAt(Instant.now());
        transaction.setAccount(account);
        transaction.setUser(user);

        return transaction;
    }

    private BigDecimal readBalance(JsonNode accountNode) {
        return accountNode.hasNonNull("balance")
                ? accountNode.get("balance").decimalValue()
                : null;
    }

    private BigDecimal readAmount(JsonNode transactionNode) {
        return transactionNode.hasNonNull("amount")
                ? transactionNode.get("amount").decimalValue()
                : null;
    }

    private LocalDate readTransactionDate(JsonNode transactionNode) {
        String date = transactionNode.path("date").asText(null);

        if (date == null || date.length() < 10) {
            return null;
        }

        return LocalDate.parse(date.substring(0, 10));
    }
}