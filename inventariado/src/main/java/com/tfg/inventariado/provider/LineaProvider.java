package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.LineaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.LineaEntity;

public interface LineaProvider {

	LineaDto convertToMapDto(LineaEntity linea);
	LineaEntity convertToMapEntity(LineaDto linea);
	List<LineaDto> listAllLineas();
	MessageResponseDto<String> addLinea(LineaDto linea);
	MessageResponseDto<String> editLinea(LineaDto linea, Integer numPedido, Integer numLinea);
	MessageResponseDto<LineaDto> getLineaById(Integer numPedido, Integer numLinea);
	
	MessageResponseDto<List<LineaDto>> listLineasByPedido(Integer numPedido);
	
	boolean lineaExisteByID(Integer numPedido, Integer numLinea);
}
