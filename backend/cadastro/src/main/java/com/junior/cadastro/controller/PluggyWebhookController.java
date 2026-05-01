package com.junior.cadastro.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cadastro.DTO.PluggyWebhookEvent;
import com.junior.cadastro.service.PluggyWebhookService;
@RestController
@RequestMapping("/webhooks/pluggy")
public class PluggyWebhookController {

    private final PluggyWebhookService pluggyWebhookService;

    @Value("${pluggy.webhook-secret}")
    private String webhookSecret;

    public PluggyWebhookController(PluggyWebhookService pluggyWebhookService) {
        this.pluggyWebhookService = pluggyWebhookService;
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String headerSecret,
            @RequestParam(value = "secret", required = false) String querySecret,
            @RequestBody PluggyWebhookEvent event
    ) {
        if (!isValidSecret(headerSecret, querySecret)) {
            return ResponseEntity.status(401).build();
        }

        pluggyWebhookService.handle(event);

        return ResponseEntity.accepted().build();
    }

    private boolean isValidSecret(String headerSecret, String querySecret) {
        String receivedSecret = headerSecret != null && !headerSecret.isBlank()
                ? headerSecret
                : querySecret;

        if (receivedSecret == null || receivedSecret.isBlank()) {
            return false;
        }

        byte[] expected = webhookSecret.getBytes(StandardCharsets.UTF_8);
        byte[] received = receivedSecret.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expected, received);
    }
}