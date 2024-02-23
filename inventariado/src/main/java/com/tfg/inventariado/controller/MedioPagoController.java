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

import com.tfg.inventariado.dto.MedioPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.provider.MedioPagoProvider;

@RestController
@RequestMapping("/medioPago")
public class MedioPagoController {

	@Autowired
	private MedioPagoProvider medioPagoProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarMedioPago(@RequestBody @Valid MedioPagoDto medioRequest) {
		try {
			MessageResponseDto<String> messageResponse = medioPagoProvider.addMedioPago(medioRequest);
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
	public ResponseEntity<List<MedioPagoDto>> listarMediosPago() {
		List<MedioPagoDto> listaDto = this.medioPagoProvider.listAllMedioPago();
		return new ResponseEntity<List<MedioPagoDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editMedioByCodigo(@PathVariable("id") String id,
			@RequestBody @Valid MedioPagoDto medioUpdate) {
		MessageResponseDto<String> messageResponse = this.medioPagoProvider.editMedioPago(medioUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<MedioPagoDto>> getMedioById(@PathVariable("id") String codigoMedio){
			MessageResponseDto<MedioPagoDto> medio = this.medioPagoProvider.getMedioPagoById(codigoMedio);
			if(medio.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(medio);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(medio.getError()));
			}
	}
	
	
}
