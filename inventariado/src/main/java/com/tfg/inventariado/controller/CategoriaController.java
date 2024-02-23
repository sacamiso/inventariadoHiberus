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

import com.tfg.inventariado.dto.CategoriaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.provider.CategoriaProvider;

@RestController
@RequestMapping("/categoria")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {
	
	@Autowired
	private CategoriaProvider categoriaProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarCategoria(@RequestBody @Valid CategoriaDto categoriaRequest) {
		try {
			MessageResponseDto<String> messageResponse = categoriaProvider.addCategoria(categoriaRequest);
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
	public ResponseEntity<List<CategoriaDto>> listarCategorias() {
		List<CategoriaDto> listaCategoriaDto = this.categoriaProvider.listAllCategoria();
		return new ResponseEntity<List<CategoriaDto>>(listaCategoriaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editCategoriaByCodigo(@PathVariable("id") String id,
			@RequestBody @Valid CategoriaDto categoriaUpdate) {
		MessageResponseDto<String> messageResponse = this.categoriaProvider.editCategoria(categoriaUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<CategoriaDto>> getCategoriaById(@PathVariable("id") String codigoCategoria){
			MessageResponseDto<CategoriaDto> categoria = this.categoriaProvider.getCategoriaById(codigoCategoria);
			if(categoria.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(categoria);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(categoria.getError()));
			}
	}
}
