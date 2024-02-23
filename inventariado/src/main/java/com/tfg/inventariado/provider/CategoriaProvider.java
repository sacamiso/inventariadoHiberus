package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.CategoriaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.CategoriaEntity;

public interface CategoriaProvider {
	
	CategoriaDto convertToMapDto(CategoriaEntity categoria);
	CategoriaEntity convertToMapEntity(CategoriaDto categoria);
	List<CategoriaDto> listAllCategoria();
	MessageResponseDto<String> addCategoria(CategoriaDto categoria);
	MessageResponseDto<String> editCategoria(CategoriaDto categoria, String codigoCategoria);
	MessageResponseDto<CategoriaDto> getCategoriaById(String codigoCategoria);
	
	boolean categoriaExisteByCodigo(String codigo);
	
}
