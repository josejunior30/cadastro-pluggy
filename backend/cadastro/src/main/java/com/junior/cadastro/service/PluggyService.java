package com.junior.cadastro.service;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.DTO.PluggyAccountDTO;
import com.junior.cadastro.DTO.PluggyTransactionDTO;
import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.PluggyTransaction;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.PluggyAccountRepository;
import com.junior.cadastro.repository.PluggyItemRepository;
import com.junior.cadastro.repository.PluggyTransactionRepository;
import com.junior.cadastro.repository.UserRepository;

@Service
public class PluggyService {

    private static final Logger log = LoggerFactory.getLogger(PluggyService.class);
    private final PluggyMapper pluggyMapper;
    private final PluggyClientService pluggyClientService;
    private final UserRepository userRepository;
    private final PluggyItemRepository itemRepository;
    private final PluggyAccountRepository accountRepository;
    private final PluggyTransactionRepository transactionRepository;

    public PluggyService(
            PluggyClientService pluggyClientService,
            PluggyMapper pluggyMapper,
            UserRepository userRepository,
            PluggyItemRepository itemRepository,
            PluggyAccountRepository accountRepository,
            PluggyTransactionRepository transactionRepository
    ) {
        this.pluggyClientService = pluggyClientService;
        this.pluggyMapper = pluggyMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public ConnectTokenResponse createConnectToken() {
        User user = getAuthenticatedUser();

        String accessToken = pluggyClientService.createConnectToken(
                String.valueOf(user.getId())
        );

        return new ConnectTokenResponse(accessToken);
    }

    @Transactional
    public void syncItem(String itemId) {
        User user = getAuthenticatedUser();

        log.info("Sincronizando item Pluggy. userId={} itemId={}", user.getId(), itemId);

        PluggyItem item = itemRepository.findByPluggyItemId(itemId)
                .orElseGet(() -> new PluggyItem(itemId, user));

        item.setUser(user);
        item.setSyncStatus("SYNCING");
        item.setLastSyncError(null);
        itemRepository.save(item);

        try {
            JsonNode accountsResponse = pluggyClientService.fetchAccounts(itemId);
            JsonNode accounts = accountsResponse != null ? accountsResponse.get("results") : null;

            if (accounts == null || !accounts.isArray()) {
                item.setSyncStatus("SUCCESS");
                item.setLastSyncAt(Instant.now());
                itemRepository.save(item);

                log.warn("Nenhuma conta retornada pela Pluggy. itemId={}", itemId);
                return;
            }

            int totalAccounts = 0;
            int totalTransactions = 0;

            for (JsonNode accountNode : accounts) {
                PluggyAccount account = saveAccount(user, item, accountNode);

                totalAccounts++;
                totalTransactions += syncTransactions(user, account);
            }

            item.setSyncStatus("SUCCESS");
            item.setLastSyncError(null);
            item.setLastSyncAt(Instant.now());
            itemRepository.save(item);

            log.info(
                    "Item Pluggy sincronizado. itemId={} accounts={} transactions={}",
                    itemId,
                    totalAccounts,
                    totalTransactions
            );

        } catch (Exception e) {
            item.setSyncStatus("ERROR");
            item.setLastSyncError(e.getMessage());
            item.setLastSyncAt(Instant.now());
            itemRepository.save(item);

            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<PluggyAccountDTO> findMyAccounts() {
        User user = getAuthenticatedUser();

        return accountRepository.findByUserOrderByNameAsc(user)
                .stream()
                .map(PluggyAccountDTO::new)
                .toList();
    }

    

	@Transactional(readOnly = true)
	public Page<PluggyTransactionDTO> findMyTransactionsByAccount(Long accountId, Pageable pageable) {
	    User user = getAuthenticatedUser();
	
	    boolean accountBelongsToUser = accountRepository.findByIdAndUser(accountId, user).isPresent();
	
	    if (!accountBelongsToUser) {
	        throw new PluggyIntegrationException("Conta não encontrada para o usuário autenticado.");
	    }
	
	    return transactionRepository
	            .findByUserAndAccountIdOrderByDateDesc(user, accountId, pageable)
	            .map(PluggyTransactionDTO::new);
	}
	
    private PluggyAccount saveAccount(User user, PluggyItem item, JsonNode accountNode) {
        String pluggyAccountId = accountNode.path("id").asText(null);

        PluggyAccount account = accountRepository.findByPluggyAccountId(pluggyAccountId)
                .orElseGet(PluggyAccount::new);

        account = pluggyMapper.toAccount(accountNode, account, item, user);

        return accountRepository.save(account);
    }
    
    
    private int syncTransactions(User user, PluggyAccount account) {
        int page = 1;
        int totalImported = 0;

        while (true) {
            JsonNode response = pluggyClientService.fetchTransactions(
                    account.getPluggyAccountId(),
                    page
            );

            JsonNode results = response != null ? response.get("results") : null;

            if (results == null || !results.isArray() || results.size() == 0) {
                break;
            }

            for (JsonNode transactionNode : results) {
                saveTransaction(user, account, transactionNode);
                totalImported++;
            }

            if (results.size() < 500) {
                break;
            }

            page++;
        }

        return totalImported;
    }

    private void saveTransaction(User user, PluggyAccount account, JsonNode transactionNode) {
        String pluggyTransactionId = transactionNode.path("id").asText(null);

        PluggyTransaction transaction = transactionRepository
                .findByPluggyTransactionId(pluggyTransactionId)
                .orElseGet(PluggyTransaction::new);

        transaction = pluggyMapper.toTransaction(transactionNode, transaction, account, user);

        transactionRepository.save(transaction);
    }
    
    
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new PluggyIntegrationException("Nenhum usuário autenticado encontrado.");
        }

        String email;

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
        } else {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            email = authentication.getName();
        }

        final String resolvedEmail = email;

        return userRepository.findByEmail(resolvedEmail)
                .orElseThrow(() -> new PluggyIntegrationException(
                        "Usuário autenticado não encontrado: " + resolvedEmail
                ));
    }

}