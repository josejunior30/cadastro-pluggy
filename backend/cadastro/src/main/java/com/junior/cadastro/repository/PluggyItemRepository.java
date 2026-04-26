package com.junior.cadastro.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.User;


public interface PluggyItemRepository extends JpaRepository<PluggyItem, Long> {
	 Optional<PluggyItem> findByPluggyItemId(String pluggyItemId);

	    List<PluggyItem> findByUser(User user);
}