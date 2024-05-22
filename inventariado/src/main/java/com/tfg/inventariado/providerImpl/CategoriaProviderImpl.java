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

import com.tfg.inventariado.dto.CategoriaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.CategoriaEntity;
import com.tfg.inventariado.provider.CategoriaProvider;
import com.tfg.inventariado.repository.CategoriaRepository;

@Service
public class CategoriaProviderImpl implements CategoriaProvider {

	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public CategoriaDto convertToMapDto(CategoriaEntity categoria) {
		return modelMapper.map(categoria, CategoriaDto.class);
	}

	@Override
	public CategoriaEntity convertToMapEntity(CategoriaDto categoria) {
		return modelMapper.map(categoria, CategoriaEntity.class);
	}

	@Override
	public List<CategoriaDto> listAllCategoria() {
		List<CategoriaEntity> listaCategoriaEntity = categoriaRepository.findAll();
		return listaCategoriaEntity.stream()
				.sorted(Comparator.comparing(CategoriaEntity::getCodigoCategoria, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addCategoria(CategoriaDto categoria) {
		Locale locale = LocaleContextHolder.getLocale();
		if(categoria.getCodigoCategoria()==null || categoria.getCodigoCategoria().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("codigoObl", null, locale));
		}
		if(categoria.getNombre()==null || categoria.getNombre().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("nombreObl", null, locale));
		}
		if(categoriaExisteByCodigo(categoria.getCodigoCategoria())) {
			return MessageResponseDto.fail(messageSource.getMessage("rolExiste", null, locale));
		}
		CategoriaEntity newCategoria = convertToMapEntity(categoria);
		newCategoria = categoriaRepository.save(newCategoria);
		return MessageResponseDto.success(messageSource.getMessage("categoriaAnadida", null, locale));
	}

	@Override
	public MessageResponseDto<String> editCategoria(CategoriaDto categoria, String codigoCategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<CategoriaEntity> optionalCategoria = categoriaRepository.findById(codigoCategoria);
		if(optionalCategoria.isPresent()) {
			CategoriaEntity categoriaToUpdate = optionalCategoria.get();
			
			this.actualizarCampos(categoriaToUpdate, categoria);
			
			categoriaRepository.save(categoriaToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("categoriaEditada", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<CategoriaDto> getCategoriaById(String codigoCategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<CategoriaEntity> optionalCategoria = categoriaRepository.findById(codigoCategoria);
		if(optionalCategoria.isPresent()) {
			CategoriaDto categoriaDto = this.convertToMapDto(optionalCategoria.get());
			return MessageResponseDto.success(categoriaDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaNoExiste", null, locale));
		}
	}

	@Override
	public boolean categoriaExisteByCodigo(String codigo) {
		Optional<CategoriaEntity> categoria = categoriaRepository.findById(codigo);
		return categoria.isPresent() ? true : false;
	}
	
	private void actualizarCampos(CategoriaEntity categoria, CategoriaDto categoriaToUpdate) {
		if(StringUtils.isNotBlank(categoriaToUpdate.getNombre())) {
			categoria.setNombre(categoriaToUpdate.getNombre());
		}
	}
}
