package com.junior.cadastro.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.junior.cadastro.DTO.RoleDTO;
import com.junior.cadastro.DTO.UserDTO;
import com.junior.cadastro.DTO.UserInsertDTO;
import com.junior.cadastro.entities.Role;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.repository.RoleRepository;
import com.junior.cadastro.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository repository;

	private final RoleRepository roleRepository;

	public UserService(UserRepository repository, RoleRepository roleRepository) {
		this.repository = repository;
		this.roleRepository = roleRepository;
	}

	@Transactional(readOnly = true)
	public List<UserDTO> findAll() {
		List<User> list = repository.findAll();
		return list.stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> user = repository.findById(id);
		User entity = user.get();
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		User entity = repository.getReferenceById(id);
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
	
		return new UserDTO(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		entity.getRoles().clear();

	    for (RoleDTO roleDto : dto.getRoles()) {
	        Role role = roleRepository.findById(roleDto.getId())
	            .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleDto.getId()));

	        entity.getRoles().add(role);
	    }

	}

}