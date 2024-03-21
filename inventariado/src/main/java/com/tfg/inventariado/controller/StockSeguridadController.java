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

import com.tfg.inventariado.dto.AvisoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.StockSeguridadDto;
import com.tfg.inventariado.dto.StockSeguridadFilterDto;
import com.tfg.inventariado.provider.StockSeguridadProvider;

@RestController
@RequestMapping("/stockSeguridad")
@CrossOrigin(origins = "http://localhost:4200")
public class StockSeguridadController {

	@Autowired
	private StockSeguridadProvider seguridadProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarStockSeguridad(@RequestBody @Valid StockSeguridadDto stockSguridadRequest) {
		
		MessageResponseDto<String> messageResponse = this.seguridadProvider.addStockSteguridad(stockSguridadRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@PostMapping("/save")
	public ResponseEntity<MessageResponseDto<?>> guardarStockSeguridad(@RequestBody @Valid List<StockSeguridadDto> stockSguridadRequest) {
		
		MessageResponseDto<String> messageResponse = this.seguridadProvider.guardarStockSeguridadOf(stockSguridadRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<StockSeguridadDto>>> listarStockPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody StockSeguridadFilterDto filtros) {
		MessageResponseListDto<List<StockSeguridadDto>> listaDto = this.seguridadProvider.listAllStockSeguridadSkipLimit(skip,limit,filtros);
		return new ResponseEntity<MessageResponseListDto<List<StockSeguridadDto>>>(listaDto, HttpStatus.OK);
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<StockSeguridadDto>> listarStockSeguridad() {
		List<StockSeguridadDto> listaDto = this.seguridadProvider.listAllStockSeguridad();
		return new ResponseEntity<List<StockSeguridadDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{idCat}/{idSubcat}/{idOf}")
	public ResponseEntity<MessageResponseDto<String>> editStockSeguridadById(@PathVariable("idCat") String idCat,@PathVariable("idSubcat") String idSubcat,@PathVariable("idOf") Integer idOf,
			 @RequestBody @Valid StockSeguridadDto ssUpadate) {
		MessageResponseDto<String> messageResponse = this.seguridadProvider.editStockSeguridad(ssUpadate, idCat,idSubcat,idOf);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{idCat}/{idSubcat}/{idOf}")
	public ResponseEntity<MessageResponseDto<StockSeguridadDto>> getStockSeguridadById(@PathVariable("idCat") String idCat,@PathVariable("idSubcat") String idSubcat,@PathVariable("idOf") Integer idOf){
			MessageResponseDto<StockSeguridadDto> ss = this.seguridadProvider.getStockSeguridadById(idCat,idSubcat,idOf);
			if(ss.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(ss);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(ss.getError()));
			}
	}
	
	@GetMapping("/listSub/{idCat}/{idSubcat}")
	public ResponseEntity<MessageResponseDto<List<StockSeguridadDto>>> listStockSeguridadBySubcategoria(@PathVariable("idCat") String idCat,@PathVariable("idSubcat") String idSubcat) {
		MessageResponseDto<List<StockSeguridadDto>> listaDto = this.seguridadProvider.listStockSeguridadBySubcategoria(idCat,idSubcat);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/listOf/{idOficina}")
	public ResponseEntity<MessageResponseDto<List<StockSeguridadDto>>> listStockSeguridadByOficina(@PathVariable("idOficina") Integer id) {
		MessageResponseDto<List<StockSeguridadDto>> listaDto = this.seguridadProvider.listStockSeguridadByOficina(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/getAvisos")
	public ResponseEntity<MessageResponseDto<List<AvisoDto>>> getAvisos() {
		MessageResponseDto<List<AvisoDto>> listaDto = this.seguridadProvider.validarStockSeguridadAvisos();
		return new ResponseEntity<MessageResponseDto<List<AvisoDto>>>(listaDto, HttpStatus.OK);
	}
}
