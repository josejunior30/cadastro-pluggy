package com.junior.cadastro.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserInsertDTO extends UserDTO{

	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "senha é obrigatória")
	@Size(min = 6, max = 128, message = "senha deve ter entre 6 e 128 caracteres")
	@Pattern(regexp = "^(?=(?:.*[A-Za-z]){6,})(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]+$", message = "senha: mínimo 6 letras, com ao menos 1 maiúscula e 1 número (somente letras/dígitos)")
	private String password;
	
	public UserInsertDTO() {
		
	}

	 public UserInsertDTO(String firstName, String lastName, String email, String password) {
	        super(firstName, lastName, email); 
	        this.password = password;
	    }
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

}