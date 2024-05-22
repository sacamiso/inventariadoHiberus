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

import com.tfg.inventariado.dto.MedioPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.MedioPagoEntity;
import com.tfg.inventariado.provider.MedioPagoProvider;
import com.tfg.inventariado.repository.MedioPagoRepository;

@Service
public class MedioPagoProviderImpl implements MedioPagoProvider {

	@Autowired
	private MedioPagoRepository medioPagoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public MedioPagoDto convertToMapDto(MedioPagoEntity medio) {
		return modelMapper.map(medio, MedioPagoDto.class);
	}

	@Override
	public MedioPagoEntity convertToMapEntity(MedioPagoDto medio) {
		return modelMapper.map(medio, MedioPagoEntity.class);
	}

	@Override
	public List<MedioPagoDto> listAllMedioPago() {
		List<MedioPagoEntity> listaMedioEntity = medioPagoRepository.findAll();
		return listaMedioEntity.stream()
				.sorted(Comparator.comparing(MedioPagoEntity::getDescripcion, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addMedioPago(MedioPagoDto medio) {
		Locale locale = LocaleContextHolder.getLocale();
		if(medio.getCodigoMedio()==null || medio.getCodigoMedio().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("codigoObl", null, locale));
		}
		if(medio.getDescripcion()==null || medio.getDescripcion().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("descripcionObligatoria", null, locale));
		}
		if(medioExisteByCodigo(medio.getCodigoMedio())) {
			return MessageResponseDto.fail(messageSource.getMessage("medioExiste", null, locale));
		}
		MedioPagoEntity newMedio = convertToMapEntity(medio);
		newMedio = medioPagoRepository.save(newMedio);
		return MessageResponseDto.success(messageSource.getMessage("medioAnadido", null, locale));
	}

	@Override
	public MessageResponseDto<String> editMedioPago(MedioPagoDto medio, String codigo) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<MedioPagoEntity> optionalMedio = medioPagoRepository.findById(codigo);
		if(optionalMedio.isPresent()) {
			MedioPagoEntity medioToUpdate = optionalMedio.get();
			
			this.actualizarCampos(medioToUpdate, medio);
			
			medioPagoRepository.save(medioToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("medioEditado", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("medioNoExiste", null, locale));
		}
	}
	
	private void actualizarCampos(MedioPagoEntity medio, MedioPagoDto medioToUpdate) {
		if(StringUtils.isNotBlank(medioToUpdate.getDescripcion())) {
			medio.setDescripcion(medioToUpdate.getDescripcion());
		}
	}

	@Override
	public MessageResponseDto<MedioPagoDto> getMedioPagoById(String codigo) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<MedioPagoEntity> optionalMedio = medioPagoRepository.findById(codigo);
		if(optionalMedio.isPresent()) {
			MedioPagoDto medioDto = this.convertToMapDto(optionalMedio.get());
			return MessageResponseDto.success(medioDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("medioNoExiste", null, locale));
		}
	}

	@Override
	public boolean medioExisteByCodigo(String codigo) {
		Optional<MedioPagoEntity> medio = medioPagoRepository.findById(codigo);
		return medio.isPresent() ? true : false;
	}

}
