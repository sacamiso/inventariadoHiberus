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

import com.tfg.inventariado.dto.EmpleadoCambioContrasena;
import com.tfg.inventariado.dto.EmpleadoDto;
import com.tfg.inventariado.dto.EmpleadoFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.provider.EmpleadoProvider;

@RestController
@RequestMapping("/empleado")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoController {
	
	@Autowired
	private EmpleadoProvider empleadoProvider;

	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarEmpleado(@RequestBody @Valid EmpleadoDto empleadoRequest) {
		try {
			MessageResponseDto<Integer> messageResponse = empleadoProvider.addEmpleado(empleadoRequest);
			
			if( messageResponse.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
			}else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(MessageResponseDto.fail(messageResponse.getError()));
			}		

		} catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<EmpleadoDto>> listarEmpleados() {
		List<EmpleadoDto> listaDto = this.empleadoProvider.listAllEmpleado();
		return new ResponseEntity<List<EmpleadoDto>>(listaDto, HttpStatus.OK);
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<EmpleadoDto>>> listarEmpleadosPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody EmpleadoFilterDto filtros) {
		MessageResponseListDto<List<EmpleadoDto>> listaDto = this.empleadoProvider.listAllEmpleadosSkipLimit(skip,limit, filtros);
		return new ResponseEntity<MessageResponseListDto<List<EmpleadoDto>>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editEmpleadoByCodigo(@PathVariable("id") Integer id,
			@RequestBody @Valid EmpleadoDto empleadoUpdate) {
		MessageResponseDto<String> messageResponse = this.empleadoProvider.editEmpleado(empleadoUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<EmpleadoDto>> getEmpleadoById(@PathVariable("id") Integer id){
		MessageResponseDto<EmpleadoDto> empleado = this.empleadoProvider.getEmpleadoById(id);
		if(empleado.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(empleado);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(empleado.getError()));
		}
	}
	
	@GetMapping("/listAll/oficina/{id}")
	public ResponseEntity<MessageResponseDto<List<EmpleadoDto>>> listEmpleadoByOficina(@PathVariable("id") Integer id){
		MessageResponseDto<List<EmpleadoDto>> listEmpleado = this.empleadoProvider.listEmpleadosByOficina(id);
		if(listEmpleado.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listEmpleado);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listEmpleado.getError()));
		}
	}
	
	@GetMapping("/listAll/rol/{id}")
	public ResponseEntity<MessageResponseDto<List<EmpleadoDto>>> listEmpleadoByRol(@PathVariable("id") String id){
		MessageResponseDto<List<EmpleadoDto>> listEmpleado = this.empleadoProvider.listEmpleadosByRol(id);
		if(listEmpleado.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listEmpleado);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listEmpleado.getError()));
		}
	}
	
	@PostMapping("/cambiarContra")
	public ResponseEntity<MessageResponseDto<String>> cambiarContrasena(@RequestBody EmpleadoCambioContrasena empleado) {
		MessageResponseDto<String> messageResponse = this.empleadoProvider.editContrasenaEmpleado(empleado);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
}
