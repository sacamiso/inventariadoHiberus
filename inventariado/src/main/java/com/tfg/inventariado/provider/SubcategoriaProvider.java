package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.SubcategoriaDto;
import com.tfg.inventariado.entity.SubcategoriaEntity;

public interface SubcategoriaProvider {
	
	SubcategoriaDto convertToMapDto(SubcategoriaEntity subcategoria);
	SubcategoriaEntity convertToMapEntity(SubcategoriaDto subcategoria);
	List<SubcategoriaDto> listAllSubcategoria();
	MessageResponseDto<String> addSubcategoria(SubcategoriaDto subcategoria);
	MessageResponseDto<String> editSubcategoria(SubcategoriaDto subcategoria, String codigoCategoria, String codigoSubcategoria);
	MessageResponseDto<SubcategoriaDto> getSubcategoriaById(String codigoCategoria, String codigoSubcategoria);
	MessageResponseDto<List<SubcategoriaDto>> listSubcategoriasDeCategoria(String codigoCategoria);
	
	boolean subcategoriaExisteByID(String codigoCategoria, String codigoSubcategoria);
}
