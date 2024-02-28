package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.entity.PedidoEntity;

public interface PedidoProvider {

	PedidoDto convertToMapDto(PedidoEntity pedido);
	PedidoEntity convertToMapEntity(PedidoDto pedido);
	List<PedidoDto> listAllPedidos();
	MessageResponseDto<String> addPedido(PedidoDto pedido);
	MessageResponseDto<String> editPedido(PedidoDto pedido, Integer id);
	MessageResponseDto<PedidoDto> getPedidoById(Integer id);
	
	MessageResponseDto<List<PedidoDto>> listPedidoByProveedor(Integer idProveedor);
	MessageResponseDto<List<PedidoDto>> listPedidoByOficina(Integer idOficina);
	
	boolean pedidoExisteByID(Integer id);
	
	MessageResponseListDto<List<PedidoDto>> listAllPedidosSkipLimit(Integer page, Integer size);
	MessageResponseDto<String> marcarRecibido(Integer id);
	
}
