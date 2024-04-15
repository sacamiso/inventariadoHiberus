package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.AsignacionDto;
import com.tfg.inventariado.dto.AsignacionFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.AsignacionEntity;

public interface AsignacionProvider {
	AsignacionDto convertToMapDto(AsignacionEntity asignacion);
	AsignacionEntity convertToMapEntity(AsignacionDto asignacion);
	List<AsignacionDto> listAllAsignacion();
	MessageResponseDto<String> addAsignacion(AsignacionDto asignacion);
	MessageResponseDto<String> editAsignacion(AsignacionDto asignacion, Integer id);
	MessageResponseDto<AsignacionDto> getAsignacionById(Integer id);
	
	MessageResponseDto<String> finalizarAsignación(Integer id);
	
	MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleado(Integer idEmpleado);
	MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoSinFinalizar(Integer idEmpleado);
	MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoFinalizadas(Integer idEmpleado);
	MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidad(Integer codUnidad);
	
	MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidadSinFinalizar(Integer codUnidad);
	
	boolean asignacionExisteByID(Integer id);
	
	MessageResponseListDto<List<AsignacionDto>> listAllAsignacionesSkipLimit(Integer page, Integer size, AsignacionFilterDto filtros);
}
