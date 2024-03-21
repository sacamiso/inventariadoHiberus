package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.AvisoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.StockSeguridadDto;
import com.tfg.inventariado.dto.StockSeguridadFilterDto;
import com.tfg.inventariado.entity.StockSeguridadEntity;

public interface StockSeguridadProvider {
	StockSeguridadDto convertToMapDto(StockSeguridadEntity seguridad);
	StockSeguridadEntity convertToMapEntity(StockSeguridadDto seguridad);
	List<StockSeguridadDto> listAllStockSeguridad();
	MessageResponseDto<String> addStockSteguridad(StockSeguridadDto seguridad);
	MessageResponseDto<String> editStockSeguridad(StockSeguridadDto seguridad, String cat, String subCat, Integer idOficina);
	MessageResponseDto<StockSeguridadDto> getStockSeguridadById(String cat, String subCat, Integer idOficina);
	
	MessageResponseDto<List<StockSeguridadDto>> listStockSeguridadByOficina(Integer idOficina);
	MessageResponseDto<List<StockSeguridadDto>> listStockSeguridadBySubcategoria(String cat, String subCat);
	
	boolean stockSeguridadExisteByID(String cat, String subCat, Integer idOficina);
	
	MessageResponseListDto<List<StockSeguridadDto>> listAllStockSeguridadSkipLimit(Integer page, Integer size, StockSeguridadFilterDto filtros);
	MessageResponseDto<String> guardarStockSeguridadOf(List<StockSeguridadDto> seguridad);
	
	MessageResponseDto<List<AvisoDto>> validarStockSeguridadAvisos();
}
