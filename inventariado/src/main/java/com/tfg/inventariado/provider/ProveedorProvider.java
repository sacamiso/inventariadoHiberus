package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.ProveedorDto;
import com.tfg.inventariado.dto.ProveedorFirterDto;
import com.tfg.inventariado.entity.ProveedorEntity;

public interface ProveedorProvider {

	ProveedorDto convertToMapDto(ProveedorEntity proveedor);
	ProveedorEntity convertToMapEntity(ProveedorDto proveedor);
	List<ProveedorDto> listAllProveedores();
	MessageResponseDto<String> addProveedor(ProveedorDto proveedor);
	MessageResponseDto<String> editProveedor(ProveedorDto proveedor, Integer id);
	MessageResponseDto<ProveedorDto> getProveedorById(Integer id);
	
	boolean proveedorExisteByID(Integer id);
	MessageResponseListDto<List<ProveedorDto>> listAllProveedoresSkipLimit(Integer page, Integer size, ProveedorFirterDto filtros);
}
