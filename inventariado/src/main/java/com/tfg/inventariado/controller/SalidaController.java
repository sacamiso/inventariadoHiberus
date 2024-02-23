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

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.SalidaDto;
import com.tfg.inventariado.provider.SalidaProvider;

@RestController
@RequestMapping("/salida")
@CrossOrigin(origins = "http://localhost:4200")
public class SalidaController {


	@Autowired
	private SalidaProvider salidaProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarSalida(@RequestBody @Valid SalidaDto salidaRequest) {
		
		MessageResponseDto<String> messageResponse = this.salidaProvider.addSalida(salidaRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<SalidaDto>> listarSalidas() {
		List<SalidaDto> listaDto = this.salidaProvider.listAllSalidas();
		return new ResponseEntity<List<SalidaDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editSalidaByCodigo(@PathVariable("id") Integer id,
			 @RequestBody @Valid SalidaDto salidaUpadate) {
		MessageResponseDto<String> messageResponse = this.salidaProvider.editSalida(salidaUpadate, id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<SalidaDto>> getSalidaById(@PathVariable("id") Integer id){
			MessageResponseDto<SalidaDto> salida = this.salidaProvider.getSalidaById( id);
			if(salida.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(salida);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(salida.getError()));
			}
	}
	
	@GetMapping("/listArt/{idArt}")
	public ResponseEntity<MessageResponseDto<List<SalidaDto>>> listHistorialByArticulo(@PathVariable("idArt") Integer id) {
		MessageResponseDto<List<SalidaDto>> listaDto = this.salidaProvider.listSalidaByArticulo(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/listOf/{idOficina}")
	public ResponseEntity<MessageResponseDto<List<SalidaDto>>> listPedidoByOficina(@PathVariable("idOficina") Integer id) {
		MessageResponseDto<List<SalidaDto>> listaDto = this.salidaProvider.listSalidaByOficina(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
}
