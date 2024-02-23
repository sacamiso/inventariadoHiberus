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

import com.tfg.inventariado.dto.CondicionPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.provider.CondicionPagoProvider;

@RestController
@RequestMapping("/condicionPago")
public class CondicionPagoController {
	
	@Autowired
	private CondicionPagoProvider condicionPagoProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarCondicionPago(@RequestBody @Valid CondicionPagoDto condicionRequest) {
		try {
			MessageResponseDto<String> messageResponse = condicionPagoProvider.addCondicionPago(condicionRequest);
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
	public ResponseEntity<List<CondicionPagoDto>> listarCondicionesPago() {
		List<CondicionPagoDto> listaDto = this.condicionPagoProvider.listAllCondicionPago();
		return new ResponseEntity<List<CondicionPagoDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editCondicionByCodigo(@PathVariable("id") String id,
			@RequestBody @Valid CondicionPagoDto condicionUpdate) {
		MessageResponseDto<String> messageResponse = this.condicionPagoProvider.editCondicionPago(condicionUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<CondicionPagoDto>> getCondicionById(@PathVariable("id") String codigoCondicion){
			MessageResponseDto<CondicionPagoDto> condicion = this.condicionPagoProvider.getCondicionPagoById(codigoCondicion);
			if(condicion.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(condicion);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(condicion.getError()));
			}
	}
	
}
