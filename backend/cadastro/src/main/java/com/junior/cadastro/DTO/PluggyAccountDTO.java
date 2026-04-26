package com.junior.cadastro.DTO;


import java.math.BigDecimal;
import java.time.Instant;

import com.junior.cadastro.entities.PluggyAccount;

public class PluggyAccountDTO {

    private Long id;
    private String pluggyAccountId;
    private String name;
    private String type;
    private String subtype;
    private String currencyCode;
    private BigDecimal balance;
    private Instant updatedAt;

    public PluggyAccountDTO() {
    }

    public PluggyAccountDTO(PluggyAccount entity) {
        this.id = entity.getId();
        this.pluggyAccountId = entity.getPluggyAccountId();
        this.name = entity.getName();
        this.type = entity.getType();
        this.subtype = entity.getSubtype();
        this.currencyCode = entity.getCurrencyCode();
        this.balance = entity.getBalance();
        this.updatedAt = entity.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getPluggyAccountId() {
        return pluggyAccountId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
