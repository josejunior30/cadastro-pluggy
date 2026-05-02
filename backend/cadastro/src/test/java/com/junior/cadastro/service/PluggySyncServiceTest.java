package com.junior.cadastro.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.entities.enuns.PluggySyncStatus;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.PluggyItemRepository;

@ExtendWith(MockitoExtension.class)
class PluggySyncServiceTest {

    @Mock
    private PluggyClientService pluggyClientService;

    @Mock
    private PluggyItemRepository itemRepository;

    @Mock
    private PluggyAccountSyncService accountSyncService;

    @Mock
    private PluggyTransactionSyncService transactionSyncService;

    private PluggySyncService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User(1L, "José", "Junior", "jose@email.com", "123");

        service = new PluggySyncService(
                pluggyClientService,
                itemRepository,
                accountSyncService,
                transactionSyncService
        );
    }

    @Test
    void syncItemShouldMarkSuccessWhenPluggyReturnsNoAccounts() throws Exception {
        PluggyItem item = new PluggyItem("item-1", authenticatedUser);

        JsonNode accountsResponse = objectMapper.readTree("""
                {
                  "results": []
                }
                """);

        when(itemRepository.findByPluggyItemId("item-1"))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(PluggyItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(pluggyClientService.fetchAccounts("item-1"))
                .thenReturn(accountsResponse);

        service.syncItemForUser(authenticatedUser, "item-1");

        ArgumentCaptor<PluggyItem> captor = ArgumentCaptor.forClass(PluggyItem.class);

        verify(itemRepository, atLeast(2)).save(captor.capture());

        PluggyItem lastSaved = captor.getAllValues()
                .get(captor.getAllValues().size() - 1);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getSyncStatus())
                .isEqualTo(PluggySyncStatus.SUCCESS);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getLastSyncError())
                .isNull();

        assertNotNull(lastSaved.getLastSyncAt());

        verify(pluggyClientService).fetchAccounts("item-1");
        verify(accountSyncService, never()).saveAccount(any(), any(), any());
        verify(transactionSyncService, never()).syncTransactions(any(), any());
    }

    @Test
    void syncItemShouldMarkSuccessWhenPluggyReturnsInvalidAccountsNode() throws Exception {
        PluggyItem item = new PluggyItem("item-1", authenticatedUser);

        JsonNode accountsResponse = objectMapper.readTree("""
                {
                  "results": null
                }
                """);

        when(itemRepository.findByPluggyItemId("item-1"))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(PluggyItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(pluggyClientService.fetchAccounts("item-1"))
                .thenReturn(accountsResponse);

        service.syncItemForUser(authenticatedUser, "item-1");

        ArgumentCaptor<PluggyItem> captor = ArgumentCaptor.forClass(PluggyItem.class);

        verify(itemRepository, atLeast(2)).save(captor.capture());

        PluggyItem lastSaved = captor.getAllValues()
                .get(captor.getAllValues().size() - 1);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getSyncStatus())
                .isEqualTo(PluggySyncStatus.SUCCESS);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getLastSyncError())
                .isNull();

        assertNotNull(lastSaved.getLastSyncAt());

        verify(pluggyClientService).fetchAccounts("item-1");
        verify(accountSyncService, never()).saveAccount(any(), any(), any());
        verify(transactionSyncService, never()).syncTransactions(any(), any());
    }

    @Test
    void syncItemShouldMarkErrorAndRethrowWhenPluggyFails() {
        PluggyItem item = new PluggyItem("item-1", authenticatedUser);

        when(itemRepository.findByPluggyItemId("item-1"))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(PluggyItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(pluggyClientService.fetchAccounts("item-1"))
                .thenThrow(new PluggyIntegrationException("Falha ao buscar contas"));

        assertThrows(
                PluggyIntegrationException.class,
                () -> service.syncItemForUser(authenticatedUser, "item-1")
        );

        ArgumentCaptor<PluggyItem> captor = ArgumentCaptor.forClass(PluggyItem.class);

        verify(itemRepository, atLeast(2)).save(captor.capture());

        PluggyItem lastSaved = captor.getAllValues()
                .get(captor.getAllValues().size() - 1);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getSyncStatus())
                .isEqualTo(PluggySyncStatus.ERROR);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getLastSyncError())
                .contains("Falha ao buscar contas");

        assertNotNull(lastSaved.getLastSyncAt());

        verify(pluggyClientService).fetchAccounts("item-1");
        verify(accountSyncService, never()).saveAccount(any(), any(), any());
        verify(transactionSyncService, never()).syncTransactions(any(), any());
    }

    @Test
    void syncItemShouldWrapUnexpectedExceptionAsPluggyIntegrationException() {
        PluggyItem item = new PluggyItem("item-1", authenticatedUser);

        when(itemRepository.findByPluggyItemId("item-1"))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(PluggyItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(pluggyClientService.fetchAccounts("item-1"))
                .thenThrow(new IllegalStateException("Erro inesperado"));

        assertThrows(
                PluggyIntegrationException.class,
                () -> service.syncItemForUser(authenticatedUser, "item-1")
        );

        ArgumentCaptor<PluggyItem> captor = ArgumentCaptor.forClass(PluggyItem.class);

        verify(itemRepository, atLeast(2)).save(captor.capture());

        PluggyItem lastSaved = captor.getAllValues()
                .get(captor.getAllValues().size() - 1);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getSyncStatus())
                .isEqualTo(PluggySyncStatus.ERROR);

        org.assertj.core.api.Assertions.assertThat(lastSaved.getLastSyncError())
                .contains("Erro inesperado");

        assertNotNull(lastSaved.getLastSyncAt());

        verify(pluggyClientService).fetchAccounts("item-1");
        verify(accountSyncService, never()).saveAccount(any(), any(), any());
        verify(transactionSyncService, never()).syncTransactions(any(), any());
    }
}