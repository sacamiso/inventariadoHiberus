package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.InventarioDto;
import com.tfg.inventariado.dto.InventarioFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.InventarioEntity;

public interface InventarioProvider {

	InventarioDto convertToMapDto(InventarioEntity inventario);
	InventarioEntity convertToMapEntity(InventarioDto inventario);
	List<InventarioDto> listAllInventarios();
	MessageResponseDto<String> addInventario(InventarioDto inventario);
	MessageResponseDto<String> editInventario(InventarioDto inventario, Integer idOf, Integer idArt);
	MessageResponseDto<InventarioDto> getInventarioById(Integer idOf, Integer idArt);
	
	MessageResponseDto<List<InventarioDto>> listInventarioByOficina(Integer idOficina);
	MessageResponseDto<List<InventarioDto>> listInventarioByArticulo(Integer idArticulo);
	
	boolean inventarioExisteByID(Integer idOf, Integer idArt);
	
	MessageResponseListDto<List<InventarioDto>> listAllInventariosSkipLimit(Integer page, Integer size, InventarioFilterDto filtros);
}
