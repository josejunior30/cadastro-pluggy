package com.junior.cadastro.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.entities.enuns.PluggySyncStatus;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.PluggyItemRepository;

import jakarta.transaction.Transactional;

@Service
public class PluggySyncService {

    private static final Logger log = LoggerFactory.getLogger(PluggySyncService.class);

    private final PluggyClientService pluggyClientService;
    private final PluggyItemRepository itemRepository;
    private final PluggyAccountSyncService accountSyncService;
    private final PluggyTransactionSyncService transactionSyncService;

    public PluggySyncService(
            PluggyClientService pluggyClientService,
            PluggyItemRepository itemRepository,
            PluggyAccountSyncService accountSyncService,
            PluggyTransactionSyncService transactionSyncService
    ) {
        this.pluggyClientService = pluggyClientService;
        this.itemRepository = itemRepository;
        this.accountSyncService = accountSyncService;
        this.transactionSyncService = transactionSyncService;
    }

    @Transactional
    public void syncItemForUser(User user, String itemId) {
        log.info("Sincronizando item Pluggy. userId={} itemId={}", user.getId(), itemId);

        PluggyItem item = getOrCreateItemSafely(itemId, user);

        item.setUser(user);
        item.setSyncStatus(PluggySyncStatus.SYNCING);
        item.setLastSyncError(null);
        itemRepository.save(item);

        try {
            JsonNode accountsResponse = pluggyClientService.fetchAccounts(itemId);
            JsonNode accounts = accountsResponse != null ? accountsResponse.get("results") : null;

            if (accounts == null || !accounts.isArray()) {
                item.setSyncStatus(PluggySyncStatus.SUCCESS);
                item.setLastSyncAt(Instant.now());
                itemRepository.save(item);
                return;
            }

            int totalAccounts = 0;
            int totalTransactions = 0;

            for (JsonNode accountNode : accounts) {
                PluggyAccount account = accountSyncService.saveAccount(user, item, accountNode);
                totalAccounts++;
                totalTransactions += transactionSyncService.syncTransactions(user, account);
            }

            item.setSyncStatus(PluggySyncStatus.SUCCESS);
            item.setLastSyncError(null);
            item.setLastSyncAt(Instant.now());
            itemRepository.save(item);

            log.info(
                    "Item Pluggy sincronizado. itemId={} accounts={} transactions={}",
                    itemId,
                    totalAccounts,
                    totalTransactions
            );

        } catch (PluggyIntegrationException e) {
            markItemAsError(item, e);
            throw e;

        } catch (Exception e) {
            markItemAsError(item, e);
            throw new PluggyIntegrationException("Erro ao sincronizar item Pluggy.", e);
        }
    }

    private PluggyItem getOrCreateItemSafely(String itemId, User user) {
        return itemRepository.findByPluggyItemId(itemId)
                .orElseGet(() -> {
                    try {
                        return itemRepository.saveAndFlush(new PluggyItem(itemId, user));

                    } catch (DataIntegrityViolationException e) {
                        return itemRepository.findByPluggyItemId(itemId)
                                .orElseThrow(() -> e);
                    }
                });
    }

    private void markItemAsError(PluggyItem item, Exception e) {
        item.setSyncStatus(PluggySyncStatus.ERROR);
        item.setLastSyncError(e.getMessage());
        item.setLastSyncAt(Instant.now());

        itemRepository.save(item);
    }
}