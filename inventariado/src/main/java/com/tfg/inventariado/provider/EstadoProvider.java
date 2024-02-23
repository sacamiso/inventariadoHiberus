package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.EstadoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.EstadoEntity;

public interface EstadoProvider {
	
	EstadoDto convertToMapDto(EstadoEntity estado);
	EstadoEntity convertToMapEntity(EstadoDto estado);
	List<EstadoDto> listAllEstados();
	MessageResponseDto<String> addEstado(EstadoDto estado);
	MessageResponseDto<String> editEstado(EstadoDto estado, String codigo);
	MessageResponseDto<EstadoDto> getEstadoById(String codigo);
	
	boolean estadoExisteByCodigo(String codigo);
}
