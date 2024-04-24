package com.tfg.inventariado.controller;

import java.io.IOException;
import java.time.LocalDateTime;
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

import com.tfg.inventariado.dto.HistorialInventarioDto;
import com.tfg.inventariado.dto.HistorialInventarioFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.provider.HistorialInventarioProvider;

@RestController
@RequestMapping("/historialInventario")
@CrossOrigin(origins = "http://localhost:4200")
public class HistorialInventarioController {

	@Autowired
	private HistorialInventarioProvider historialProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarHistorial(@RequestBody @Valid HistorialInventarioDto historialRequest) {
		
		MessageResponseDto<String> messageResponse = this.historialProvider.addHistorial(historialRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<HistorialInventarioDto>> listarHistorial() {
		List<HistorialInventarioDto> listaDto = this.historialProvider.listAllHistoriales();
		return new ResponseEntity<List<HistorialInventarioDto>>(listaDto, HttpStatus.OK);
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<HistorialInventarioDto>>> listarHitorialPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody HistorialInventarioFilterDto filtros) {
		MessageResponseListDto<List<HistorialInventarioDto>> listaDto = this.historialProvider.listAllHistorialSkipLimit(skip,limit,filtros);
		return new ResponseEntity<MessageResponseListDto<List<HistorialInventarioDto>>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{idOf}/{idArt}/{fecha}")
	public ResponseEntity<MessageResponseDto<String>> editHistorialByCodigo(@PathVariable("idOf") Integer idOf, @PathVariable("idArt") Integer idArt,
			@PathVariable("fecha") LocalDateTime fecha, @RequestBody @Valid HistorialInventarioDto historialUpadate) {
		MessageResponseDto<String> messageResponse = this.historialProvider.editHistorial(historialUpadate, idOf, idArt, fecha);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{idOf}/{idArt}/{fecha}")
	public ResponseEntity<MessageResponseDto<HistorialInventarioDto>> getHistorialById(@PathVariable("idOf") Integer idOf, @PathVariable("idArt") Integer idArt,
			@PathVariable("fecha") LocalDateTime fecha){
			MessageResponseDto<HistorialInventarioDto> historial = this.historialProvider.getHistorialById( idOf, idArt, fecha);
			if(historial.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(historial);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(historial.getError()));
			}
	}
	
	@GetMapping("/listArt/{idArt}")
	public ResponseEntity<MessageResponseDto<List<HistorialInventarioDto>>> listHistorialByArticulo(@PathVariable("idArt") Integer id) {
		MessageResponseDto<List<HistorialInventarioDto>> listaDto = this.historialProvider.listHistorialByArticulo(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/listOf/{idOficina}")
	public ResponseEntity<MessageResponseDto<List<HistorialInventarioDto>>> listPedidoByOficina(@PathVariable("idOficina") Integer id) {
		MessageResponseDto<List<HistorialInventarioDto>> listaDto = this.historialProvider.listHistorialByOficina(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	
	@PostMapping("/descargarExcel")
	public byte[] descargarExcelHistorialInventario(@RequestBody HistorialInventarioFilterDto filtros)throws IOException{
		try {
			return this.historialProvider.descargarExcelHistorialInventario(filtros);
		} catch (IOException e) {
			throw e;
		}
	}
}
