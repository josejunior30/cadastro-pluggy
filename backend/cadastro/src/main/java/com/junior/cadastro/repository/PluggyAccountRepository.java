package com.junior.cadastro.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.User;


public interface PluggyAccountRepository extends JpaRepository<PluggyAccount, Long> {


    Optional<PluggyAccount> findByPluggyAccountId(String pluggyAccountId);

    List<PluggyAccount> findByUser(User user);
}