package com.tfg.inventariado.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.inventariado.dto.AuthRequestDto;
import com.tfg.inventariado.dto.AuthResponseDto;
import com.tfg.inventariado.dto.EmpleadoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.provider.EmpleadoProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

	@Autowired
	private EmpleadoProvider authProvider;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login (@RequestBody @Valid AuthRequestDto authRequest) {
		
		AuthResponseDto jwtDto = authProvider.login(authRequest);
		return ResponseEntity.ok(jwtDto);
	}
	
	@GetMapping("/user")
	public MessageResponseDto<EmpleadoDto> getLoggedUser(@AuthenticationPrincipal EmpleadoEntity user) {
		if (user != null) {
			EmpleadoDto usuarioDto = modelMapper.map(user, EmpleadoDto.class);
			return MessageResponseDto.success(usuarioDto);
		}
		return MessageResponseDto.fail("Error al obtener el usuario logueado");
	}
}
