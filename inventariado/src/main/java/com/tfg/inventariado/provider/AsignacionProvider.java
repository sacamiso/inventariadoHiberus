package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.AsignacionDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.AsignacionEntity;

public interface AsignacionProvider {
	AsignacionDto convertToMapDto(AsignacionEntity asignacion);
	AsignacionEntity convertToMapEntity(AsignacionDto asignacion);
	List<AsignacionDto> listAllAsignacion();
	MessageResponseDto<String> addAsignación(AsignacionDto asignacion);
	MessageResponseDto<String> editAsignación(AsignacionDto asignacion, Integer id);
	MessageResponseDto<AsignacionDto> getAsignacionById(Integer id);
	
	MessageResponseDto<String> finalizarAsignación(Integer id);
	
	MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleado(Integer idEmpleado);
	MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoSinFinalizar(Integer idEmpleado);
	MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoFinalizadas(Integer idEmpleado);
	MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidad(Integer codUnidad);
	
	MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidadSinFinalizar(Integer codUnidad);
	
	boolean asignacionExisteByID(Integer id);
}
