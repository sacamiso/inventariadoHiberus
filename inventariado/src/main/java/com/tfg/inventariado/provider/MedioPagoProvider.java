package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MedioPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.MedioPagoEntity;

public interface MedioPagoProvider {

	MedioPagoDto convertToMapDto(MedioPagoEntity medio);
	MedioPagoEntity convertToMapEntity(MedioPagoDto medio);
	List<MedioPagoDto> listAllMedioPago();
	MessageResponseDto<String> addMedioPago(MedioPagoDto medio);
	MessageResponseDto<String> editMedioPago(MedioPagoDto medio, String codigo);
	MessageResponseDto<MedioPagoDto> getMedioPagoById(String codigo);
	
	boolean medioExisteByCodigo(String codigo);
}
