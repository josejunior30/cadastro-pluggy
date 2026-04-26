package com.junior.cadastro.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class PluggyClientService {

    private static final Logger log = LoggerFactory.getLogger(PluggyClientService.class);

    private final RestClient restClient;

    @Value("${pluggy.client-id}")
    private String clientId;

    @Value("${pluggy.client-secret}")
    private String clientSecret;

    private String cachedApiKey;
    private Instant cachedApiKeyExpiresAt;

    public PluggyClientService(
            RestClient.Builder builder,
            @Value("${pluggy.base-url}") String baseUrl
    ) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public String createConnectToken(String clientUserId) {
        try {
            String apiKey = getApiKey();

            Map<String, Object> options = new HashMap<>();
            options.put("clientUserId", clientUserId);
            options.put("avoidDuplicates", true);

            Map<String, Object> body = Map.of("options", options);

            log.info("Criando connect token da Pluggy para clientUserId={}", clientUserId);

            JsonNode response = restClient.post()
                    .uri("/connect_token")
                    .header("X-API-KEY", apiKey)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            String accessToken = response.path("accessToken").asText(null);

            if (accessToken == null || accessToken.isBlank()) {
                log.warn("Pluggy não retornou accessToken para clientUserId={}", clientUserId);
                throw new RuntimeException("Pluggy não retornou accessToken.");
            }

            log.info("Connect token da Pluggy criado com sucesso para clientUserId={}", clientUserId);

            return accessToken;

        } catch (RestClientResponseException e) {
            log.error(
                    "Erro ao criar connect token na Pluggy. Status={} Body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new RuntimeException("Erro ao criar connect token na Pluggy.", e);
        }
    }

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

            log.info("Contas recebidas da Pluggy para itemId={}", itemId);

            return response;

        } catch (RestClientResponseException e) {
            log.error(
                    "Erro ao buscar contas na Pluggy. itemId={} Status={} Body={}",
                    itemId,
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new RuntimeException("Erro ao buscar contas na Pluggy.", e);
        }
    }

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

            throw new RuntimeException("Erro ao buscar transações na Pluggy.", e);
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

            String apiKey = response.path("apiKey").asText(null);

            if (apiKey == null || apiKey.isBlank()) {
                log.warn("Pluggy não retornou apiKey na autenticação.");
                throw new RuntimeException("Não foi possível autenticar na Pluggy.");
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

            throw new RuntimeException("Erro ao autenticar na Pluggy.", e);
        }
    }
}