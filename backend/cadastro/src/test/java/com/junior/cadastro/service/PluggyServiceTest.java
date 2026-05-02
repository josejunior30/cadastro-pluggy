package com.junior.cadastro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.PluggyAccountRepository;
import com.junior.cadastro.repository.PluggyItemRepository;
import com.junior.cadastro.repository.PluggyTransactionRepository;

@ExtendWith(MockitoExtension.class)
class PluggyServiceTest {

    @Mock
    private PluggyClientService pluggyClientService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private PluggySyncService pluggySyncService;

    @Mock
    private PluggyAccountRepository accountRepository;

    @Mock
    private PluggyItemRepository itemRepository;

    @Mock
    private PluggyTransactionRepository transactionRepository;

    @InjectMocks
    private PluggyService service;

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User(1L, "José", "Junior", "jose@email.com", "123");
    }

    @Test
    void createConnectTokenShouldUseAuthenticatedUser() {
        when(currentUserService.getAuthenticatedUser())
                .thenReturn(authenticatedUser);

        when(pluggyClientService.createConnectToken("1"))
                .thenReturn("pluggy-token");

        ConnectTokenResponse response = service.createConnectToken();

        assertNotNull(response);
        assertEquals("pluggy-token", response.getAccessToken());

        verify(currentUserService).getAuthenticatedUser();
        verify(pluggyClientService).createConnectToken("1");
    }

    @Test
    void syncItemShouldUseAuthenticatedUserAndDelegateToPluggySyncService() {
        when(currentUserService.getAuthenticatedUser())
                .thenReturn(authenticatedUser);

        service.syncItem("item-1");

        verify(currentUserService).getAuthenticatedUser();
        verify(pluggySyncService).syncItemForUser(authenticatedUser, "item-1");
    }

    @Test
    void syncItemShouldRethrowWhenPluggySyncServiceFails() {
        when(currentUserService.getAuthenticatedUser())
                .thenReturn(authenticatedUser);

        doThrow(new PluggyIntegrationException("Falha ao sincronizar item Pluggy."))
                .when(pluggySyncService)
                .syncItemForUser(authenticatedUser, "item-1");

        assertThrows(
                PluggyIntegrationException.class,
                () -> service.syncItem("item-1")
        );

        verify(currentUserService).getAuthenticatedUser();
        verify(pluggySyncService).syncItemForUser(authenticatedUser, "item-1");
    }

    @Test
    void findMyTransactionsByAccountShouldThrowWhenAccountDoesNotBelongToUser() {
        when(currentUserService.getAuthenticatedUser())
                .thenReturn(authenticatedUser);

        when(accountRepository.findByIdAndUser(99L, authenticatedUser))
                .thenReturn(Optional.empty());

        assertThrows(
                PluggyIntegrationException.class,
                () -> service.findMyTransactionsByAccount(99L, PageRequest.of(0, 20))
        );

        verify(transactionRepository, never())
                .findByUserAndAccountIdOrderByDateDesc(any(), anyLong(), any());
    }
}