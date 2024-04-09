package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.ArticuloFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.ArticuloEntity;

public interface ArticuloProvider {
	
	ArticuloDto convertToMapDto(ArticuloEntity articulo);
	ArticuloEntity convertToMapEntity(ArticuloDto articulo);
	List<ArticuloDto> listAllArticulo();
	MessageResponseDto<String> addArticulo(ArticuloDto articulo);
	MessageResponseDto<String> editArticulo(ArticuloDto articulo, Integer articuloId);
	MessageResponseDto<ArticuloDto> getArticuloById(Integer articuloId);
	MessageResponseDto<List<ArticuloDto>> listArticulosByCategoria(String codigoCategoria);
	MessageResponseDto<List<ArticuloDto>> listArticulosBySubcategoria(String codigoCategoria, String codigoSubcategoria);

	boolean articuloExisteByID(Integer articuloId);
	
	MessageResponseListDto<List<ArticuloDto>> listAllArticulosSkipLimit(Integer page, Integer size, ArticuloFilterDto filtros);

}
