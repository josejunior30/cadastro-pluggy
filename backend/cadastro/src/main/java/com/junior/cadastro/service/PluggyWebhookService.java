package com.junior.cadastro.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.junior.cadastro.DTO.PluggyWebhookEvent;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
@Service
public class PluggyWebhookService {

    private static final Logger log = LoggerFactory.getLogger(PluggyWebhookService.class);

    private final PluggyService pluggyService;

    public PluggyWebhookService(PluggyService pluggyService) {
        this.pluggyService = pluggyService;
    }

    @Async
    public void handle(PluggyWebhookEvent event) {
        try {
            if (event == null) {
                log.warn("Webhook Pluggy recebido com body vazio.");
                return;
            }

            log.info(
                    "Webhook Pluggy recebido. event={} eventId={} itemId={} clientUserId={} triggeredBy={}",
                    event.event(),
                    event.eventId(),
                    event.itemId(),
                    event.clientUserId(),
                    event.triggeredBy()
            );

            if (event.event() == null || event.event().isBlank()) {
                log.warn("Webhook Pluggy sem campo event. eventId={}", event.eventId());
                return;
            }

            switch (event.event()) {
                case "item/created", "item/updated", "transactions/created", "transactions/updated" -> {
                    pluggyService.syncItemFromWebhook(event.itemId(), event.clientUserId());
                }

                case "item/error" -> {
                    pluggyService.markItemAsErrorFromWebhook(event.itemId(), event.error());
                }

                case "item/deleted" -> {
                    pluggyService.markItemAsDeletedFromWebhook(event.itemId());
                }

                case "item/waiting_user_input", "item/login_succeeded" -> {
                    log.info("Evento Pluggy informativo recebido: {} itemId={}", event.event(), event.itemId());
                }

                default -> {
                    log.info("Evento Pluggy ignorado: {}", event.event());
                }
            }

        } catch (PluggyIntegrationException e) {
            log.error("Erro de integração ao processar webhook Pluggy: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar webhook Pluggy.", e);
        }
    }
}