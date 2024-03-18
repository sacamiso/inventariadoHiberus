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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.inventariado.dto.InventarioDto;
import com.tfg.inventariado.dto.InventarioFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.provider.InventarioProvider;

@RestController
@RequestMapping("/inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class InventarioController {
	
	@Autowired
	private InventarioProvider inventarioProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarInventario(@RequestBody @Valid InventarioDto inventarioRequest) {
		
		MessageResponseDto<String> messageResponse = this.inventarioProvider.addInventario(inventarioRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<InventarioDto>>> listarInventarioPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody InventarioFilterDto filtros) {
		MessageResponseListDto<List<InventarioDto>> listaDto = this.inventarioProvider.listAllInventariosSkipLimit(skip,limit,filtros);
		return new ResponseEntity<MessageResponseListDto<List<InventarioDto>>>(listaDto, HttpStatus.OK);
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<InventarioDto>> listarInventario() {
		List<InventarioDto> listaDto = this.inventarioProvider.listAllInventarios();
		return new ResponseEntity<List<InventarioDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{idOf}/{idArt}")
	public ResponseEntity<MessageResponseDto<String>> editInventarioByCodigo(@PathVariable("idOf") Integer idOf, @PathVariable("idArt") Integer idArt,
			 @RequestBody @Valid InventarioDto inventarioUpadate) {
		MessageResponseDto<String> messageResponse = this.inventarioProvider.editInventario(inventarioUpadate, idOf, idArt);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{idOf}/{idArt}")
	public ResponseEntity<MessageResponseDto<InventarioDto>> getInventarioById(@PathVariable("idOf") Integer idOf, @PathVariable("idArt") Integer idArt){
			MessageResponseDto<InventarioDto> inventario = this.inventarioProvider.getInventarioById( idOf, idArt);
			if(inventario.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(inventario);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(inventario.getError()));
			}
	}
	
	@GetMapping("/listArt/{idArt}")
	public ResponseEntity<MessageResponseDto<List<InventarioDto>>> listInventarioByArticulo(@PathVariable("idArt") Integer id) {
		MessageResponseDto<List<InventarioDto>> listaDto = this.inventarioProvider.listInventarioByArticulo(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/listOf/{idOficina}")
	public ResponseEntity<MessageResponseDto<List<InventarioDto>>> listInventarioByOficina(@PathVariable("idOficina") Integer id) {
		MessageResponseDto<List<InventarioDto>> listaDto = this.inventarioProvider.listInventarioByOficina(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
}
