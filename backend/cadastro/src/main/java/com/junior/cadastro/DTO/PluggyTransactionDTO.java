package com.junior.cadastro.DTO;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.junior.cadastro.entities.PluggyTransaction;

public class PluggyTransactionDTO {

    private Long id;
    private String pluggyTransactionId;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private String category;
    private String status;
    private String type;
    private Instant importedAt;

    private Long accountId;
    private String accountName;

    public PluggyTransactionDTO() {
    }

    public PluggyTransactionDTO(PluggyTransaction entity) {
        this.id = entity.getId();
        this.pluggyTransactionId = entity.getPluggyTransactionId();
        this.date = entity.getDate();
        this.description = entity.getDescription();
        this.amount = entity.getAmount();
        this.currencyCode = entity.getCurrencyCode();
        this.category = entity.getCategory();
        this.status = entity.getStatus();
        this.type = entity.getType();
        this.importedAt = entity.getImportedAt();

        if (entity.getAccount() != null) {
            this.accountId = entity.getAccount().getId();
            this.accountName = entity.getAccount().getName();
        }
    }

    public Long getId() {
        return id;
    }

    public String getPluggyTransactionId() {
        return pluggyTransactionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public Instant getImportedAt() {
        return importedAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }
}