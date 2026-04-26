package com.junior.cadastro.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.PluggyTransaction;
import com.junior.cadastro.entities.User;

public interface PluggyTransactionRepository extends JpaRepository<PluggyTransaction, Long> {

	  Optional<PluggyTransaction> findByPluggyTransactionId(String pluggyTransactionId);

	    List<PluggyTransaction> findByUserOrderByDateDesc(User user);

	    List<PluggyTransaction> findByUserAndAccountIdOrderByDateDesc(User user, Long accountId);
	    
	    Page<PluggyTransaction> findByUserAndAccountIdOrderByDateDesc(
	            User user,
	            Long accountId,
	            Pageable pageable
	    );
}
