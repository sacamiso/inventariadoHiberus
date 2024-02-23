package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.SalidaDto;
import com.tfg.inventariado.entity.SalidaEntity;

public interface SalidaProvider {

	SalidaDto convertToMapDto(SalidaEntity salida);
	SalidaEntity convertToMapEntity(SalidaDto salida);
	List<SalidaDto> listAllSalidas();
	MessageResponseDto<String> addSalida(SalidaDto salida);
	MessageResponseDto<String> editSalida(SalidaDto salida, Integer id);
	MessageResponseDto<SalidaDto> getSalidaById(Integer id);
	
	MessageResponseDto<List<SalidaDto>> listSalidaByOficina(Integer idOficina);
	MessageResponseDto<List<SalidaDto>> listSalidaByArticulo(Integer idArticulo);
	
	boolean salidaExisteByID(Integer id);
}
