package com.junior.cadastro.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyTransaction;
import com.junior.cadastro.entities.User;

public interface PluggyTransactionRepository extends JpaRepository<PluggyTransaction, Long> {

    Optional<PluggyTransaction> findByPluggyTransactionId(String pluggyTransactionId);

    List<PluggyTransaction> findByUser(User user);

    List<PluggyTransaction> findByAccount(PluggyAccount account);
}