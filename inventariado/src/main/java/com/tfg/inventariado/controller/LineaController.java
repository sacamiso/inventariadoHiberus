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

import com.tfg.inventariado.dto.LineaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.provider.LineaProvider;

@RestController
@RequestMapping("/linea")
public class LineaController {

	@Autowired
	private LineaProvider lineaProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarLinea(@RequestBody @Valid LineaDto lineaRequest) {
		
		MessageResponseDto<String> messageResponse = this.lineaProvider.addLinea(lineaRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<LineaDto>> listarLineas() {
		List<LineaDto> listaDto = this.lineaProvider.listAllLineas();
		return new ResponseEntity<List<LineaDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{numPedido}/{numLinea}")
	public ResponseEntity<MessageResponseDto<String>> editLineaByCodigo(@PathVariable("numPedido") Integer numPedido, @PathVariable("numLinea") Integer numLinea,
			 @RequestBody @Valid LineaDto lineaUpadate) {
		MessageResponseDto<String> messageResponse = this.lineaProvider.editLinea(lineaUpadate, numPedido, numLinea);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{numPedido}/{numLinea}")
	public ResponseEntity<MessageResponseDto<LineaDto>> getLineaById(@PathVariable("numPedido") Integer numPedido, @PathVariable("numLinea") Integer numLinea){
			MessageResponseDto<LineaDto> linea = this.lineaProvider.getLineaById( numPedido, numLinea);
			if(linea.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(linea);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(linea.getError()));
			}
	}
	
	@GetMapping("/listPedido/{numPedido}")
	public ResponseEntity<MessageResponseDto<List<LineaDto>>> listLineasByPedido(@PathVariable("numPedido") Integer id) {
		MessageResponseDto<List<LineaDto>> listaDto = this.lineaProvider.listLineasByPedido(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
}
