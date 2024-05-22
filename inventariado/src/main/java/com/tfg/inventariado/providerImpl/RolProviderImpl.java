package com.tfg.inventariado.providerImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
	
	@Autowired
    private MessageSource messageSource;
	
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
		Locale locale = LocaleContextHolder.getLocale();
		if(rol.getCodigoRol()==null || rol.getCodigoRol().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("codigoObl", null, locale));
		}
		if(rol.getNombre()==null || rol.getNombre().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("nombreObl", null, locale));
		}
		if(rolExisteByCodigo(rol.getCodigoRol())) {
			return MessageResponseDto.fail(messageSource.getMessage("rolExiste", null, locale));
		}
		RolEntity newRol = convertToMapEntity(rol);
		newRol = rolRepository.save(newRol);
		return MessageResponseDto.success(messageSource.getMessage("rolAnadido", null, locale));
	}

	@Override
	public MessageResponseDto<String> editRol(RolDto rol, String codigo) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<RolEntity> optionalRol = rolRepository.findById(codigo);
		if(optionalRol.isPresent()) {
			RolEntity rolToUpdate = optionalRol.get();
			
			this.actualizarCampos(rolToUpdate, rol);
			
			rolRepository.save(rolToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("rolEditado", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("rolNoExiste", null, locale));
		}
	}
	
	private void actualizarCampos(RolEntity rol, RolDto rolToUpdate) {
		if(StringUtils.isNotBlank(rolToUpdate.getNombre())) {
			rol.setNombre(rolToUpdate.getNombre());
		}
	}

	@Override
	public MessageResponseDto<RolDto> getRolById(String codigo) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<RolEntity> optionalRol = rolRepository.findById(codigo);
		if(optionalRol.isPresent()) {
			RolDto rolDto = this.convertToMapDto(optionalRol.get());
			return MessageResponseDto.success(rolDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("rolNoExiste", null, locale));
		}
	}

	@Override
	public boolean rolExisteByCodigo(String codigo) {
		Optional<RolEntity> optionalRol = rolRepository.findById(codigo);
		return optionalRol.isPresent() ? true : false;	
	}

}
