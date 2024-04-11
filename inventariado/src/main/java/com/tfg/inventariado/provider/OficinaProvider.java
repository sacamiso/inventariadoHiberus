package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.OficinaDto;
import com.tfg.inventariado.dto.OficinaFilterDto;
import com.tfg.inventariado.entity.OficinaEntity;

public interface OficinaProvider {
	OficinaDto convertToMapDto(OficinaEntity oficina);
	OficinaEntity convertToMapEntity(OficinaDto oficina);
	List<OficinaDto> listAllOficinas();
	MessageResponseDto<String> addOficina(OficinaDto oficina);
	MessageResponseDto<String> editOficina(OficinaDto oficina, Integer id);
	MessageResponseDto<OficinaDto> getOficinaById(Integer id);
	
	boolean oficinaExisteByID(Integer id);
	MessageResponseListDto<List<OficinaDto>> listAllOficinasSkipLimit(Integer page, Integer size, OficinaFilterDto filtros);

}
