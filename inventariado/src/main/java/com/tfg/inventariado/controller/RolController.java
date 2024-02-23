package com.tfg.inventariado.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.RolDto;
import com.tfg.inventariado.provider.RolProvider;

@RestController
@RequestMapping("/rol")
public class RolController {

	@Autowired
	private RolProvider rolProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarRol(@RequestBody @Valid RolDto rolRequest) {
		try {
			MessageResponseDto<String> messageResponse = rolProvider.addRol(rolRequest);
			if (messageResponse.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(MessageResponseDto.fail(messageResponse.getError()));
			}

		} catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<RolDto>> listarRoles() {
		List<RolDto> listaDto = this.rolProvider.listAllRol();
		return new ResponseEntity<List<RolDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editRolByCodigo(@PathVariable("id") String id,
			@RequestBody @Valid RolDto rolUpdate) {
		MessageResponseDto<String> messageResponse = this.rolProvider.editRol(rolUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<RolDto>> getMedioById(@PathVariable("id") String codigo){
			MessageResponseDto<RolDto> rol = this.rolProvider.getRolById(codigo);
			if(rol.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(rol);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(rol.getError()));
			}
	}
}
