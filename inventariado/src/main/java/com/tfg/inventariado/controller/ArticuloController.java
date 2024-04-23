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

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.ArticuloFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.provider.ArticuloProvider;

@RestController
@RequestMapping("/articulo")
@CrossOrigin(origins = "http://localhost:4200")
public class ArticuloController {
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarArticulo(@RequestBody @Valid ArticuloDto articuloRequest) {
		try {
			MessageResponseDto<Integer> messageResponse = articuloProvider.addArticulo(articuloRequest);
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
	public ResponseEntity<List<ArticuloDto>> listarArticulos() {
		List<ArticuloDto> listArticuloDto = this.articuloProvider.listAllArticulo();
		return new ResponseEntity<List<ArticuloDto>>(listArticuloDto, HttpStatus.OK);
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<ArticuloDto>>> listarArticulosPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody ArticuloFilterDto filtros) {
		MessageResponseListDto<List<ArticuloDto>> listaDto = this.articuloProvider.listAllArticulosSkipLimit(skip,limit, filtros);
		return new ResponseEntity<MessageResponseListDto<List<ArticuloDto>>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editArticuloByCodigo(@PathVariable("id") Integer id,
			@RequestBody @Valid ArticuloDto articuloUpdate) {
		MessageResponseDto<String> messageResponse = this.articuloProvider.editArticulo(articuloUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<ArticuloDto>> getArticuloById(@PathVariable("id") Integer id){
			MessageResponseDto<ArticuloDto> articulo = this.articuloProvider.getArticuloById(id);
			if(articulo.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(articulo);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(articulo.getError()));
			}
	}
	
	@GetMapping("/list/{idCat}")
	public ResponseEntity<MessageResponseDto<List<ArticuloDto>>> listarArticulosByCategoria(@PathVariable("idCat") String cat) {
		MessageResponseDto<List<ArticuloDto>> listArticuloDto = this.articuloProvider.listArticulosByCategoria(cat);
		if(listArticuloDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listArticuloDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listArticuloDto.getError()));
		}
	}
	
	@GetMapping("/list/{idCat}/{idSubcat}")
	public ResponseEntity<MessageResponseDto<List<ArticuloDto>>>  listarArticulosBySubategoria(@PathVariable("idCat") String cat, @PathVariable("idSubcat") String sub) {
		
		MessageResponseDto<List<ArticuloDto>> listArticuloDto = this.articuloProvider.listArticulosBySubcategoria(cat, sub);
		if(listArticuloDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listArticuloDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listArticuloDto.getError()));
		}
	}
	

}
