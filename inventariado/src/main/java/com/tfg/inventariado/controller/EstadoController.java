package com.tfg.inventariado.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.inventariado.dto.EstadoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.provider.EstadoProvider;

@RestController
@RequestMapping("/estado")
@CrossOrigin(origins = "http://localhost:4200")
public class EstadoController {

	@Autowired
	private EstadoProvider estadoProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarEstado(@RequestBody @Valid EstadoDto estadoRequest) {
		try {
			MessageResponseDto<String> messageResponse = estadoProvider.addEstado(estadoRequest);
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
	public ResponseEntity<List<EstadoDto>> listarEstados() {
		List<EstadoDto> listaDto = this.estadoProvider.listAllEstados();
		return new ResponseEntity<List<EstadoDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editEstadoByCodigo(@PathVariable("id") String id,
			@RequestBody @Valid EstadoDto estadoUpdate) {
		MessageResponseDto<String> messageResponse = this.estadoProvider.editEstado(estadoUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<EstadoDto>> getEstadoById(@PathVariable("id") String codigo){
			MessageResponseDto<EstadoDto> estado = this.estadoProvider.getEstadoById(codigo);
			if(estado.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(estado);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(estado.getError()));
			}
	}
}
