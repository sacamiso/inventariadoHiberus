package com.tfg.inventariado.providerImpl;

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

import com.tfg.inventariado.dto.EstadoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.EstadoEntity;
import com.tfg.inventariado.provider.EstadoProvider;
import com.tfg.inventariado.repository.EstadoRepository;

@Service
public class EstadoProviderImpl implements EstadoProvider {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public EstadoDto convertToMapDto(EstadoEntity estado) {
		return modelMapper.map(estado, EstadoDto.class);
	}

	@Override
	public EstadoEntity convertToMapEntity(EstadoDto estado) {
		return modelMapper.map(estado, EstadoEntity.class);
	}

	@Override
	public List<EstadoDto> listAllEstados() {
		List<EstadoEntity> listaEntity = estadoRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addEstado(EstadoDto estado) {
		Locale locale = LocaleContextHolder.getLocale();
		if(estado.getCodigoEstado()==null || estado.getCodigoEstado().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("codigoObl", null, locale));
		}
		if(estado.getNombre()==null || estado.getNombre().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("nombreObl", null, locale));
		}
		if(estadoExisteByCodigo(estado.getCodigoEstado())) {
			return MessageResponseDto.fail(messageSource.getMessage("estadoExiste", null, locale));
		}
		EstadoEntity newEstado = convertToMapEntity(estado);
		newEstado = estadoRepository.save(newEstado);
		return MessageResponseDto.success(messageSource.getMessage("estadoAnadido", null, locale));
	}

	@Override
	public MessageResponseDto<String> editEstado(EstadoDto estado, String codigo) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<EstadoEntity> optionalEstado = estadoRepository.findById(codigo);
		if(optionalEstado.isPresent()) {
			EstadoEntity estadoToUpdate = optionalEstado.get();
			
			this.actualizarCampos(estadoToUpdate, estado);
			
			estadoRepository.save(estadoToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("estadoEditado", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("estadoNoExiste", null, locale));
		}
	}
	
	private void actualizarCampos(EstadoEntity estado, EstadoDto estadoToUpdate) {
		if(StringUtils.isNotBlank(estadoToUpdate.getNombre())) {
			estado.setNombre(estadoToUpdate.getNombre());
		}
	}

	@Override
	public MessageResponseDto<EstadoDto> getEstadoById(String codigo) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<EstadoEntity> optionalEstado = estadoRepository.findById(codigo);
		if(optionalEstado.isPresent()) {
			EstadoDto estadoDto = this.convertToMapDto(optionalEstado.get());
			return MessageResponseDto.success(estadoDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("estadoNoExiste", null, locale));
		}
	}

	@Override
	public boolean estadoExisteByCodigo(String codigo) {
		Optional<EstadoEntity> optionalEstado = estadoRepository.findById(codigo);
		return optionalEstado.isPresent() ? true : false;	
	}

}
