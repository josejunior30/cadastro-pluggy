package com.junior.cadastro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.junior.cadastro.DTO.RoleDTO;
import com.junior.cadastro.DTO.UserDTO;
import com.junior.cadastro.DTO.UserInsertDTO;
import com.junior.cadastro.entities.Role;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.DatabaseException;
import com.junior.cadastro.exceptions.EmailAlreadyExistsException;
import com.junior.cadastro.exceptions.ResourceNotFoundException;
import com.junior.cadastro.repository.RoleRepository;
import com.junior.cadastro.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<User> list = repository.findAll();
        return list.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. Id: " + id));

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Já existe um usuário cadastrado com este email.");
        }

        User entity = new User();
        copyDtoToEntity(dto, entity);

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = repository.save(entity);

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);

            if (emailBelongsToAnotherUser(dto.getEmail(), id)) {
                throw new EmailAlreadyExistsException("Já existe outro usuário cadastrado com este email.");
            }

            copyDtoToEntity(dto, entity);

            entity = repository.save(entity);

            return new UserDTO(entity);

        } catch (EntityNotFoundException | JpaObjectRetrievalFailureException e) {
            throw new ResourceNotFoundException("Usuário não encontrado. Id: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado. Id: " + id);
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar este usuário porque ele está relacionado a outros registros.");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();

        for (RoleDTO roleDto : dto.getRoles()) {
            Role role = roleRepository.findById(roleDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada. Id: " + roleDto.getId()));

            entity.getRoles().add(role);
        }
    }

    private boolean emailBelongsToAnotherUser(String email, Long userId) {
        return repository.findByEmail(email)
                .filter(user -> !user.getId().equals(userId))
                .isPresent();
    }
}