package com.junior.cadastro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.DTO.PluggySyncRequest;
import com.junior.cadastro.service.PluggyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pluggy")
public class PluggyController {

    private final PluggyService pluggyService;

    public PluggyController(PluggyService pluggyService) {
        this.pluggyService = pluggyService;
    }

    @PostMapping("/connect-token")
    public ResponseEntity<ConnectTokenResponse> createConnectToken() {
        return ResponseEntity.ok(pluggyService.createConnectToken());
    }

    @PostMapping("/items/sync")
    public ResponseEntity<Void> syncItem(@Valid @RequestBody PluggySyncRequest request) {
        pluggyService.syncItem(request.getItemId());
        return ResponseEntity.noContent().build();
    }
}
