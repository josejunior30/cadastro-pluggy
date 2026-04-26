package com.junior.cadastro.DTO;

import jakarta.validation.constraints.NotBlank;

public class PluggySyncRequest {

    @NotBlank
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}