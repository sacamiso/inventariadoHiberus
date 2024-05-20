package com.tfg.inventariado.providerImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.RolDto;
import com.tfg.inventariado.entity.RolEntity;
import com.tfg.inventariado.provider.RolProvider;
import com.tfg.inventariado.repository.RolRepository;

@Service
public class RolProviderImpl implements RolProvider {

	@Autowired
	private RolRepository rolRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public RolDto convertToMapDto(RolEntity rol) {
		return modelMapper.map(rol, RolDto.class);
	}

	@Override
	public RolEntity convertToMapEntity(RolDto rol) {
		return modelMapper.map(rol, RolEntity.class);
	}

	@Override
	public List<RolDto> listAllRol() {
		List<RolEntity> listaEntity = rolRepository.findAll();
		return listaEntity.stream()
				.sorted(Comparator.comparing(RolEntity::getCodigoRol, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addRol(RolDto rol) {
		if(rol.getCodigoRol()==null || rol.getCodigoRol().isEmpty()) {
			return MessageResponseDto.fail("El código es obligatorio");
		}
		if(rol.getNombre()==null || rol.getNombre().isEmpty()) {
			return MessageResponseDto.fail("el nombre es obligatorio");
		}
		if(rolExisteByCodigo(rol.getCodigoRol())) {
			return MessageResponseDto.fail("El rol ya existe");
		}
		RolEntity newRol = convertToMapEntity(rol);
		newRol = rolRepository.save(newRol);
		return MessageResponseDto.success("Rol añadido con éxito");
	}

	@Override
	public MessageResponseDto<String> editRol(RolDto rol, String codigo) {
		Optional<RolEntity> optionalRol = rolRepository.findById(codigo);
		if(optionalRol.isPresent()) {
			RolEntity rolToUpdate = optionalRol.get();
			
			this.actualizarCampos(rolToUpdate, rol);
			
			rolRepository.save(rolToUpdate);
			
			return MessageResponseDto.success("Rol editado con éxito");
			
		}else {
			return MessageResponseDto.fail("El rol que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(RolEntity rol, RolDto rolToUpdate) {
		if(StringUtils.isNotBlank(rolToUpdate.getNombre())) {
			rol.setNombre(rolToUpdate.getNombre());
		}
	}

	@Override
	public MessageResponseDto<RolDto> getRolById(String codigo) {
		Optional<RolEntity> optionalRol = rolRepository.findById(codigo);
		if(optionalRol.isPresent()) {
			RolDto rolDto = this.convertToMapDto(optionalRol.get());
			return MessageResponseDto.success(rolDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ningún rol con ese código");
		}
	}

	@Override
	public boolean rolExisteByCodigo(String codigo) {
		Optional<RolEntity> optionalRol = rolRepository.findById(codigo);
		return optionalRol.isPresent() ? true : false;	
	}

}
