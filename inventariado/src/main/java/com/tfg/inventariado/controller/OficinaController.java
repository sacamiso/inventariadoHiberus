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
import com.tfg.inventariado.dto.OficinaDto;
import com.tfg.inventariado.provider.OficinaProvider;

@RestController
@RequestMapping("/oficina")
@CrossOrigin(origins = "http://localhost:4200")
public class OficinaController {

	@Autowired
	private OficinaProvider oficinaProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarOficina(@RequestBody @Valid OficinaDto oficinaRequest) {
		try {
			MessageResponseDto<String> messageResponse = oficinaProvider.addOficina(oficinaRequest);
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
	public ResponseEntity<List<OficinaDto>> listarOficinas() {
		List<OficinaDto> listaDto = this.oficinaProvider.listAllOficinas();
		return new ResponseEntity<List<OficinaDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editOficinaById(@PathVariable("id") Integer id,
			@RequestBody @Valid OficinaDto oficinaUpdate) {
		MessageResponseDto<String> messageResponse = this.oficinaProvider.editOficina(oficinaUpdate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<OficinaDto>> getOficinaById(@PathVariable("id") Integer id){
			MessageResponseDto<OficinaDto> oficina = this.oficinaProvider.getOficinaById(id);
			if(oficina.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(oficina);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(oficina.getError()));
			}
	}
}
