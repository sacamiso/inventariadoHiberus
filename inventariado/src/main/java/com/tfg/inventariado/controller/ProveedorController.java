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

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.ProveedorDto;
import com.tfg.inventariado.dto.ProveedorFirterDto;
import com.tfg.inventariado.provider.ProveedorProvider;

@RestController
@RequestMapping("/proveedor")
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {

	@Autowired
	private ProveedorProvider proveedorProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarProveedor(@RequestBody @Valid ProveedorDto provedorRequest) {
		
		MessageResponseDto<Integer> messageResponse = this.proveedorProvider.addProveedor(provedorRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
		
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<ProveedorDto>> listarProveedores() {
		List<ProveedorDto> listaDto = this.proveedorProvider.listAllProveedores();
		return new ResponseEntity<List<ProveedorDto>>(listaDto, HttpStatus.OK);
	}
	
	@PostMapping("/listAllPag")
	public ResponseEntity<MessageResponseListDto<List<ProveedorDto>>> listarProveedoresPag(@RequestParam(value = "limit", required = false) Integer limit,
		    @RequestParam(value = "skip", required = false) Integer skip, @RequestBody ProveedorFirterDto filtros) {
		MessageResponseListDto<List<ProveedorDto>> listaDto = this.proveedorProvider.listAllProveedoresSkipLimit(skip,limit, filtros);
		return new ResponseEntity<MessageResponseListDto<List<ProveedorDto>>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editProveedorByCodigo(@PathVariable("id") Integer id,
			@RequestBody @Valid ProveedorDto proveedorUpdate) {
		MessageResponseDto<String> messageResponse = this.proveedorProvider.editProveedor(proveedorUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<ProveedorDto>> getProveedorById(@PathVariable("id") Integer codigo){
			MessageResponseDto<ProveedorDto> proveedor = this.proveedorProvider.getProveedorById(codigo);
			if(proveedor.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(proveedor);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(proveedor.getError()));
			}
	}
}
