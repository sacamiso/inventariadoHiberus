package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.dto.UnidadDto;
import com.tfg.inventariado.dto.UnidadFilterDto;
import com.tfg.inventariado.entity.UnidadEntity;

public interface UnidadProvider {

	UnidadDto convertToMapDto(UnidadEntity unidad);
	UnidadEntity convertToMapEntity(UnidadDto unidad);
	List<UnidadDto> listAllUnidades();
	MessageResponseDto<String> addUnidad(UnidadDto unidad);
	MessageResponseDto<String> editUnidad(UnidadDto unidad, Integer id);
	MessageResponseDto<UnidadDto> getUnidadById(Integer id);
	
	MessageResponseDto<List<UnidadDto>> listUnidadByEstado(String codEstado);
	MessageResponseDto<List<UnidadDto>> listUnidadDisponibles();
	MessageResponseDto<List<UnidadDto>> listUnidadDisponiblesByOficina(Integer idOficina);
	MessageResponseDto<List<UnidadDto>> listUnidadNODisponibles();
	MessageResponseDto<List<UnidadDto>> listUnidadNODisponiblesByOficina(Integer idOficina);
	MessageResponseDto<List<UnidadDto>> listUnidadesByOficina(Integer idOficina);
	MessageResponseDto<List<UnidadDto>> listUnidadByArticulo(Integer idArticulo);
	
	MessageResponseDto<String> darSalidaUnidad(Integer idSalida, Integer idUnidad);
	
	boolean unidadExisteByID(Integer id);
	
	MessageResponseListDto<List<UnidadDto>> listAllUnidadesSkipLimit(Integer page, Integer size, UnidadFilterDto filtros);
	List<ArticuloDto> listaArticulosDisponiblesEnInventarioParaRegistrarUnidadesByOficina(Integer idOficina);
	MessageResponseDto<List<PedidoDto>> pedidosDisponiblesByOficinaAndArticulo(Integer idOficina, Integer codArticulo);
	
	MessageResponseDto<List<UnidadDto>> listUnidadDisponiblesSinAsignarByOficina(Integer idOficina);

}
