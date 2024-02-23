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
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.provider.PedidoProvider;

@RestController
@RequestMapping("/pedido")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

	@Autowired
	private PedidoProvider pedidoProvider;
	
	@PostMapping("/add")
	public ResponseEntity<MessageResponseDto<?>> agregarPedido(@RequestBody @Valid PedidoDto pedidoRequest) {
		
		MessageResponseDto<String> messageResponse = this.pedidoProvider.addPedido(pedidoRequest);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<PedidoDto>> listarPedidos() {
		List<PedidoDto> listaDto = this.pedidoProvider.listAllPedidos();
		return new ResponseEntity<List<PedidoDto>>(listaDto, HttpStatus.OK);
	}
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<MessageResponseDto<String>> editPedidoByCodigo(@PathVariable("id") Integer id,
			@RequestBody @Valid PedidoDto pedidoUpadate) {
		MessageResponseDto<String> messageResponse = this.pedidoProvider.editPedido(pedidoUpadate,id);

		if (messageResponse.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageResponseDto.fail(messageResponse.getError()));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MessageResponseDto<PedidoDto>> getPedidoById(@PathVariable("id") Integer codigo){
			MessageResponseDto<PedidoDto> pedido = this.pedidoProvider.getPedidoById(codigo);
			if(pedido.isSuccess()) {
				return ResponseEntity.status(HttpStatus.OK).body(pedido);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(MessageResponseDto.fail(pedido.getError()));
			}
	}
	
	@GetMapping("/listProv/{idProveedor}")
	public ResponseEntity<MessageResponseDto<List<PedidoDto>>> listPedidoByProveedor(@PathVariable("idProveedor") Integer id) {
		MessageResponseDto<List<PedidoDto>> listaPedidoDto = this.pedidoProvider.listPedidoByProveedor(id);
		if(listaPedidoDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaPedidoDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaPedidoDto.getError()));
		}
	}
	
	@GetMapping("/listOf/{idOficina}")
	public ResponseEntity<MessageResponseDto<List<PedidoDto>>> listPedidoByOficina(@PathVariable("idOficina") Integer id) {
		MessageResponseDto<List<PedidoDto>> listaPedidoDto = this.pedidoProvider.listPedidoByOficina(id);
		if(listaPedidoDto.isSuccess()) {
			return ResponseEntity.status(HttpStatus.OK).body(listaPedidoDto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(MessageResponseDto.fail(listaPedidoDto.getError()));
		}
	}
}
