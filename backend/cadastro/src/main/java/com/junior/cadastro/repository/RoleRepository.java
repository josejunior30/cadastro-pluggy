package com.junior.cadastro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.Role;

public interface RoleRepository extends  JpaRepository<Role, Long>{
	

}