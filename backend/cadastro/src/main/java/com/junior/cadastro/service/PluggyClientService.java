package com.junior.cadastro.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.exceptions.PluggyIntegrationException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
@Service
public class PluggyClientService {

    private static final Logger log = LoggerFactory.getLogger(PluggyClientService.class);

    private final RestClient restClient;

    @Value("${pluggy.client-id}")
    private String clientId;

    @Value("${pluggy.client-secret}")
    private String clientSecret;

    @Value("${pluggy.webhook-url:}")
    private String webhookUrl;

    private String cachedApiKey;
    private Instant cachedApiKeyExpiresAt;

    public PluggyClientService(
            RestClient.Builder builder,
            @Value("${pluggy.base-url}") String baseUrl
    ) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Retry(name = "pluggy", fallbackMethod = "fallbackCreateConnectToken")
    @CircuitBreaker(name = "pluggy")
    public String createConnectToken(String clientUserId) {
        try {
            String apiKey = getApiKey();

            Map<String, Object> options = new HashMap<>();
            options.put("clientUserId", clientUserId);
            options.put("avoidDuplicates", true);

            if (webhookUrl != null && !webhookUrl.isBlank()) {
                options.put("webhookUrl", webhookUrl);
            }

            Map<String, Object> body = Map.of("options", options);

            log.info("Criando connect token da Pluggy para clientUserId={}", clientUserId);

            JsonNode response = restClient.post()
                    .uri("/connect_token")
                    .header("X-API-KEY", apiKey)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                log.warn("Pluggy retornou body vazio ao criar connect token. clientUserId={}", clientUserId);
                throw new PluggyIntegrationException("Pluggy retornou resposta vazia ao criar connect token.");
            }

            String accessToken = response.path("accessToken").asText(null);

            if (accessToken == null || accessToken.isBlank()) {
                log.warn("Pluggy não retornou accessToken para clientUserId={}", clientUserId);
                throw new PluggyIntegrationException("Pluggy não retornou accessToken.");
            }

            log.info("Connect token da Pluggy criado com sucesso para clientUserId={}", clientUserId);

            return accessToken;

        } catch (RestClientResponseException e) {
            log.error(
                    "Erro ao criar connect token na Pluggy. Status={} Body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new PluggyIntegrationException("Erro ao criar connect token na Pluggy.", e);

        } catch (ResourceAccessException e) {
            log.error("Timeout ou falha de conexão ao criar connect token na Pluggy.", e);

            throw new PluggyIntegrationException("Pluggy indisponível ou sem resposta ao criar token de conexão.", e);

        } catch (RestClientException e) {
            log.error("Erro inesperado no client HTTP ao criar connect token na Pluggy.", e);

            throw new PluggyIntegrationException("Erro inesperado na comunicação com a Pluggy.", e);
        }
    }

    @Retry(name = "pluggy", fallbackMethod = "fallbackFetchAccounts")
    @CircuitBreaker(name = "pluggy")
    public JsonNode fetchAccounts(String itemId) {
        try {
            log.info("Buscando contas na Pluggy para itemId={}", itemId);

            JsonNode response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/accounts")
                            .queryParam("itemId", itemId)
                            .build())
                    .header("X-API-KEY", getApiKey())
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                log.warn("Pluggy retornou body vazio ao buscar contas. itemId={}", itemId);
                throw new PluggyIntegrationException("Pluggy retornou resposta vazia ao buscar contas.");
            }

            log.info("Contas recebidas da Pluggy para itemId={}", itemId);

            return response;

        } catch (RestClientResponseException e) {
            log.error(
                    "Erro ao buscar contas na Pluggy. itemId={} Status={} Body={}",
                    itemId,
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new PluggyIntegrationException("Erro ao buscar contas na Pluggy.", e);

        } catch (ResourceAccessException e) {
            log.error("Timeout ou falha de conexão ao buscar contas na Pluggy. itemId={}", itemId, e);

            throw new PluggyIntegrationException("Pluggy indisponível ou sem resposta ao buscar contas.", e);

        } catch (RestClientException e) {
            log.error("Erro inesperado no client HTTP ao buscar contas na Pluggy. itemId={}", itemId, e);

            throw new PluggyIntegrationException("Erro inesperado na comunicação com a Pluggy.", e);
        }
    }

    @Retry(name = "pluggy", fallbackMethod = "fallbackFetchTransactions")
    @CircuitBreaker(name = "pluggy")
    public JsonNode fetchTransactions(String accountId, int page) {
        try {
            log.info("Buscando transações na Pluggy para accountId={} page={}", accountId, page);

            JsonNode response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/transactions")
                            .queryParam("accountId", accountId)
                            .queryParam("pageSize", 500)
                            .queryParam("page", page)
                            .build())
                    .header("X-API-KEY", getApiKey())
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                log.warn(
                        "Pluggy retornou body vazio ao buscar transações. accountId={} page={}",
                        accountId,
                        page
                );

                throw new PluggyIntegrationException("Pluggy retornou resposta vazia ao buscar transações.");
            }

            log.info("Transações recebidas da Pluggy para accountId={} page={}", accountId, page);

            return response;

        } catch (RestClientResponseException e) {
            log.error(
                    "Erro ao buscar transações na Pluggy. accountId={} page={} Status={} Body={}",
                    accountId,
                    page,
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new PluggyIntegrationException("Erro ao buscar transações na Pluggy.", e);

        } catch (ResourceAccessException e) {
            log.error(
                    "Timeout ou falha de conexão ao buscar transações na Pluggy. accountId={} page={}",
                    accountId,
                    page,
                    e
            );

            throw new PluggyIntegrationException("Pluggy indisponível ou sem resposta ao buscar transações.", e);

        } catch (RestClientException e) {
            log.error(
                    "Erro inesperado no client HTTP ao buscar transações na Pluggy. accountId={} page={}",
                    accountId,
                    page,
                    e
            );

            throw new PluggyIntegrationException("Erro inesperado na comunicação com a Pluggy.", e);
        }
    }

    private String getApiKey() {
        if (cachedApiKey != null
                && cachedApiKeyExpiresAt != null
                && Instant.now().isBefore(cachedApiKeyExpiresAt)) {
            return cachedApiKey;
        }

        try {
            log.info("Autenticando na Pluggy para obter API key.");

            Map<String, String> body = Map.of(
                    "clientId", clientId,
                    "clientSecret", clientSecret
            );

            JsonNode response = restClient.post()
                    .uri("/auth")
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                log.warn("Pluggy retornou body vazio na autenticação.");
                throw new PluggyIntegrationException("Pluggy retornou resposta vazia na autenticação.");
            }

            String apiKey = response.path("apiKey").asText(null);

            if (apiKey == null || apiKey.isBlank()) {
                log.warn("Pluggy não retornou apiKey na autenticação.");
                throw new PluggyIntegrationException("Não foi possível autenticar na Pluggy.");
            }

            cachedApiKey = apiKey;
            cachedApiKeyExpiresAt = Instant.now().plusSeconds(60 * 110);

            log.info("Autenticação na Pluggy realizada com sucesso.");

            return cachedApiKey;

        } catch (RestClientResponseException e) {
            log.error(
                    "Erro ao autenticar na Pluggy. Status={} Body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new PluggyIntegrationException("Erro ao autenticar na Pluggy.", e);

        } catch (ResourceAccessException e) {
            log.error("Timeout ou falha de conexão ao autenticar na Pluggy.", e);

            throw new PluggyIntegrationException("Pluggy indisponível ou sem resposta na autenticação.", e);

        } catch (RestClientException e) {
            log.error("Erro inesperado no client HTTP ao autenticar na Pluggy.", e);

            throw new PluggyIntegrationException("Erro inesperado na comunicação com a Pluggy.", e);
        }
    }

    @SuppressWarnings("unused")
    private String fallbackCreateConnectToken(String clientUserId, Throwable e) {
        log.error(
                "Fallback createConnectToken acionado. clientUserId={} erro={}",
                clientUserId,
                e.getClass().getSimpleName()
        );

        throw new PluggyIntegrationException("Pluggy indisponível ao criar token de conexão.", e);
    }

    @SuppressWarnings("unused")
    private JsonNode fallbackFetchAccounts(String itemId, Throwable e) {
        log.error(
                "Fallback fetchAccounts acionado. itemId={} erro={}",
                itemId,
                e.getClass().getSimpleName()
        );

        throw new PluggyIntegrationException("Pluggy indisponível ao buscar contas.", e);
    }

    @SuppressWarnings("unused")
    private JsonNode fallbackFetchTransactions(String accountId, int page, Throwable e) {
        log.error(
                "Fallback fetchTransactions acionado. accountId={} page={} erro={}",
                accountId,
                page,
                e.getClass().getSimpleName()
        );

        throw new PluggyIntegrationException("Pluggy indisponível ao buscar transações.", e);
    }
}