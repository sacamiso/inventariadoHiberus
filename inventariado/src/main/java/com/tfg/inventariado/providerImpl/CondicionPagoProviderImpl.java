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

import com.tfg.inventariado.dto.CondicionPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.CondicionPagoEntity;
import com.tfg.inventariado.provider.CondicionPagoProvider;
import com.tfg.inventariado.repository.CondicionPagoRepository;

@Service
public class CondicionPagoProviderImpl implements CondicionPagoProvider {

	@Autowired
	private CondicionPagoRepository condicionPagoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public CondicionPagoDto convertToMapDto(CondicionPagoEntity condicion) {
		return modelMapper.map(condicion, CondicionPagoDto.class);
	}

	@Override
	public CondicionPagoEntity convertToMapEntity(CondicionPagoDto condicion) {
		return modelMapper.map(condicion, CondicionPagoEntity.class);
	}

	@Override
	public List<CondicionPagoDto> listAllCondicionPago() {
		List<CondicionPagoEntity> listaCondicionEntity = condicionPagoRepository.findAll();
		return listaCondicionEntity.stream()
				.sorted(Comparator.comparing(CondicionPagoEntity::getDescripcion, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addCondicionPago(CondicionPagoDto condicion) {
		Locale locale = LocaleContextHolder.getLocale();
		if(condicion.getCodigoCondicion()==null || condicion.getCodigoCondicion().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("codigoObl", null, locale));
		}
		if(condicion.getDescripcion()==null || condicion.getDescripcion().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("descripcionObligatoria", null, locale));
		}
		if(condicionExisteByCodigo(condicion.getCodigoCondicion())) {
			return MessageResponseDto.fail(messageSource.getMessage("condicionExiste", null, locale));
		}
		CondicionPagoEntity newCondicion = convertToMapEntity(condicion);
		newCondicion = condicionPagoRepository.save(newCondicion);
		return MessageResponseDto.success(messageSource.getMessage("condicionAnadida", null, locale));
	}

	@Override
	public MessageResponseDto<String> editCondicionPago(CondicionPagoDto condicion, String codigoCondicion) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<CondicionPagoEntity> optionalCondicion = condicionPagoRepository.findById(codigoCondicion);
		if(optionalCondicion.isPresent()) {
			CondicionPagoEntity condicionToUpdate = optionalCondicion.get();
			
			this.actualizarCampos(condicionToUpdate, condicion);
			
			condicionPagoRepository.save(condicionToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("condicionEditada", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("condicionNoExiste", null, locale));
		}
	}

	private void actualizarCampos(CondicionPagoEntity condicion, CondicionPagoDto condicionToUpdate) {
		if(StringUtils.isNotBlank(condicionToUpdate.getDescripcion())) {
			condicion.setDescripcion(condicionToUpdate.getDescripcion());
		}
	}
	
	@Override
	public MessageResponseDto<CondicionPagoDto> getCondicionPagoById(String codigoCondicion) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<CondicionPagoEntity> optionalCondicion = condicionPagoRepository.findById(codigoCondicion);
		if(optionalCondicion.isPresent()) {
			CondicionPagoDto condicionDto = this.convertToMapDto(optionalCondicion.get());
			return MessageResponseDto.success(condicionDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("condicionNoExiste", null, locale));
		}
	}

	@Override
	public boolean condicionExisteByCodigo(String codigo) {
		Optional<CondicionPagoEntity> condicion = condicionPagoRepository.findById(codigo);
		return condicion.isPresent() ? true : false;
	}

}
