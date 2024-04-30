package com.tfg.inventariado.provider;

import java.io.IOException;
import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.dto.PedidoFilterDto;
import com.tfg.inventariado.entity.PedidoEntity;
import com.tfg.inventariado.entity.PedidoVWEntity;

public interface PedidoProvider {

	PedidoDto convertToMapDto(PedidoEntity pedido);
	PedidoEntity convertToMapEntity(PedidoDto pedido);
	PedidoDto convertToMapDtoVista(PedidoVWEntity pedido);
	PedidoVWEntity convertToMapEntityVista(PedidoDto pedido);
	List<PedidoDto> listAllPedidos();
	MessageResponseDto<?> addPedido(PedidoDto pedido);
	MessageResponseDto<String> editPedido(PedidoDto pedido, Integer id);
	MessageResponseDto<PedidoDto> getPedidoById(Integer id);
	
	MessageResponseDto<List<PedidoDto>> listPedidoByProveedor(Integer idProveedor);
	MessageResponseDto<List<PedidoDto>> listPedidoByOficina(Integer idOficina);
	
	boolean pedidoExisteByID(Integer id);
	
	MessageResponseListDto<List<PedidoDto>> listAllPedidosSkipLimit(Integer page, Integer size, PedidoFilterDto filtros);
	MessageResponseDto<String> marcarRecibido(Integer id);
	
	MessageResponseDto<String> devolverPedido(Integer id);
	byte[] descargarExcelPedido(PedidoFilterDto filtros) throws IOException;
	
	byte[] descargarExcelPedidoById(Integer id) throws IOException;
	
	void generarPDFById(Integer id);
}
