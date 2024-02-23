package com.tfg.inventariado.provider;

import java.util.List;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.RolDto;
import com.tfg.inventariado.entity.RolEntity;

public interface RolProvider {
	
	RolDto convertToMapDto(RolEntity rol);
	RolEntity convertToMapEntity(RolDto rol);
	List<RolDto> listAllRol();
	MessageResponseDto<String> addRol(RolDto rol);
	MessageResponseDto<String> editRol(RolDto rol, String codigo);
	MessageResponseDto<RolDto> getRolById(String codigo);
	
	boolean rolExisteByCodigo(String codigo);
}
