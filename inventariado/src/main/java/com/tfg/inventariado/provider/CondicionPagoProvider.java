package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.CondicionPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.CondicionPagoEntity;

public interface CondicionPagoProvider {
	
	CondicionPagoDto convertToMapDto(CondicionPagoEntity condicion);
	CondicionPagoEntity convertToMapEntity(CondicionPagoDto condicion);
	List<CondicionPagoDto> listAllCondicionPago();
	MessageResponseDto<String> addCondicionPago(CondicionPagoDto condicion);
	MessageResponseDto<String> editCondicionPago(CondicionPagoDto condicion, String codigoCondicion);
	MessageResponseDto<CondicionPagoDto> getCondicionPagoById(String codigoCondicion);
	
	boolean condicionExisteByCodigo(String codigo);
}
