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

import com.tfg.inventariado.dto.AsignacionDto;
import com.tfg.inventariado.dto.AsignacionFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.provider.AsignacionProvider;

@RestController
@RequestMapping("/asignacion")
@CrossOrigin(origins = "http://localhost:4200")
public class AsignacionController {
	
	@Autowired
	private AsignacionProvider asignacionProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarAsignacion(@RequestBody @Valid AsignacionDto asignacionRequest) {
		
		MessageResponseDto<String> messageResponse = this.asignacionProvider.addAsignacion(asignacionRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<AsignacionDto>> listarAsignación() {
		List<AsignacionDto> listaDto = this.asignacionProvider.listAllAsignacion();
		return new ResponseEntity<List<AsignacionDto>>(listaDto, HttpStatus.OK);
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<AsignacionDto>>> listarAsignacionesPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody AsignacionFilterDto filtros) {
		MessageResponseListDto<List<AsignacionDto>> listaDto = this.asignacionProvider.listAllAsignacionesSkipLimit(skip,limit,filtros);
		return new ResponseEntity<MessageResponseListDto<List<AsignacionDto>>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editAsignacionById(@PathVariable("id") Integer id,
			 @RequestBody @Valid AsignacionDto asignacionUpadate) {
		MessageResponseDto<String> messageResponse = this.asignacionProvider.editAsignacion(asignacionUpadate, id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<AsignacionDto>> getAsignacionById(@PathVariable("id") Integer id){
			MessageResponseDto<AsignacionDto> asignacion = this.asignacionProvider.getAsignacionById(id);
			if(asignacion.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(asignacion);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(asignacion.getError()));
			}
	}
	
	@PutMapping("/finalizar/{id}")
	public ResponseEntity<MessageResponseDto<String>> finalizarAsignacionById(@PathVariable("id") Integer id) {
		MessageResponseDto<String> messageResponse = this.asignacionProvider.finalizarAsignación(id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/empleado/{idE}")
	public ResponseEntity<MessageResponseDto<List<AsignacionDto>>> listAsignacionByEmpleado(@PathVariable("idE") Integer id) {
		MessageResponseDto<List<AsignacionDto>> listaDto = this.asignacionProvider.listAsignacionByEmpleado(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}

	@GetMapping("/empleadoSinFinalizar/{idE}")
	public ResponseEntity<MessageResponseDto<List<AsignacionDto>>> listAsignacionByEmpleadoSinFinalizar(@PathVariable("idE") Integer id) {
		MessageResponseDto<List<AsignacionDto>> listaDto = this.asignacionProvider.listAsignacionByEmpleadoSinFinalizar(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/empleadoFinalizada/{idE}")
	public ResponseEntity<MessageResponseDto<List<AsignacionDto>>> listAsignacionByEmpleadoFinalizadas(@PathVariable("idE") Integer id) {
		MessageResponseDto<List<AsignacionDto>> listaDto = this.asignacionProvider.listAsignacionByEmpleadoFinalizadas(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
	
	@GetMapping("/unidad/{idU}")
	public ResponseEntity<MessageResponseDto<List<AsignacionDto>>> listAsignacionByUnidad(@PathVariable("idU") Integer id) {
		MessageResponseDto<List<AsignacionDto>> listaDto = this.asignacionProvider.listAsignacionByUnidad(id);
		if(listaDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaDto.getError()));
		}
	}
}
