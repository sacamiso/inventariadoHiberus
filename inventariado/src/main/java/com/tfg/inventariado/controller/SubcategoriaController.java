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
import com.tfg.inventariado.dto.SubcategoriaDto;
import com.tfg.inventariado.provider.SubcategoriaProvider;

@RestController
@RequestMapping("/subcategoria")
public class SubcategoriaController {

	@Autowired
	private SubcategoriaProvider subcategoriaProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarSubcategoria(@RequestBody @Valid SubcategoriaDto subcategoriaRequest) {
		try {
			
			MessageResponseDto<String> messageResponse = subcategoriaProvider.addSubcategoria(subcategoriaRequest);
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
	public ResponseEntity<List<SubcategoriaDto>> listarSubcategorias() {
		List<SubcategoriaDto> listaSubcategoriaDto = this.subcategoriaProvider.listAllSubcategoria();
		return new ResponseEntity<List<SubcategoriaDto>>(listaSubcategoriaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{idCat}/{idSubcat}")
	public ResponseEntity<MessageResponseDto<String>> editSubcategoriaByCodigo(@PathVariable("idCat") String cat, @PathVariable("idSubcat") String subcat,
			@RequestBody @Valid SubcategoriaDto subcategoriaUpdate) {
		MessageResponseDto<String> messageResponse = this.subcategoriaProvider.editSubcategoria(subcategoriaUpdate,cat,subcat);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{idCat}/{idSubcat}")
	public ResponseEntity<MessageResponseDto<SubcategoriaDto>> getSubcategoriaById(@PathVariable("idCat") String cat, @PathVariable("idSubcat") String subcat){
			MessageResponseDto<SubcategoriaDto> subcategoria = this.subcategoriaProvider.getSubcategoriaById(cat,subcat);
			if(subcategoria.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(subcategoria);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(subcategoria.getError()));
			}
	}
	
	@GetMapping("/list/{idCat}")
	public ResponseEntity<MessageResponseDto<List<SubcategoriaDto>>> listarSubcategoriasByCategoria(@PathVariable("idCat") String cat) {
		MessageResponseDto<List<SubcategoriaDto>> listaSubcategoriaDto = this.subcategoriaProvider.listSubcategoriasDeCategoria(cat);
		if(listaSubcategoriaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaSubcategoriaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaSubcategoriaDto.getError()));
		}
	}
	
}
